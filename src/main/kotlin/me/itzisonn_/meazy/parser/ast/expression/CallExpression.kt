package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.parent
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ConstructorSymbol
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
import me.itzisonn_.meazy.runtime.environment.*
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import kotlin.uuid.Uuid

class CallExpression(
    val id: String,
    val args: List<Expression>
) : Expression, LocalStatement {
    override val children = args.toSet()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val resolvedCallable = resolveCallable(environment)

        if (resolvedCallable.isFunction) {
            var endLabel: Uuid? = null

            if (resolvedCallable.target != null) {
                resolvedCallable.target.emit(instructions, environment)

                val parent = parent
                if (parent is MemberExpression) {
                    if (!parent.isNullSafe) {
                        if (resolvedCallable.target.getType(environment).isNullable) {
                            throw RuntimeException("Unsafe member access of function '$id' on object of type ${resolvedCallable.classDesc.descriptorString()}")
                        }
                    }
                    else {
                        val nonnullLabel = instructions.createAndInitLabel()
                        endLabel = instructions.createAndInitLabel()

                        instructions.duplicate()
                        instructions.gotoLabelIfNonNull(nonnullLabel)

                        instructions.pop()
                        instructions.loadNull()
                        instructions.gotoLabel(endLabel)

                        instructions.bindLabel(nonnullLabel)
                    }
                }
            }

            instructions.invokeMethod(
                resolvedCallable.classDesc,
                id,
                resolvedCallable.methodTypeDesc,
                if (resolvedCallable.target == null)
                    if (resolvedCallable.isInterface) InvokeType.STATIC_INTERFACE else InvokeType.STATIC
                    else if (resolvedCallable.isInterface) InvokeType.INTERFACE else InvokeType.VIRTUAL
            ) {
                for (i in args.indices) {
                    val parameterType = resolvedCallable.methodTypeDesc.parameterType(i)

                    val arg = args[i]
                    val argType = arg.getType(environment).classDesc

                    arg.emit(this, environment)

                    if (!environment.isInstanceOf(argType, parameterType)) {
                        if (!instructions.convertPrimitiveOrBoxed(argType, parameterType)) {
                            throw RuntimeException("Can't pass argument of type $argType to parameter of type $parameterType")
                        }
                    }
                }
            }

            if (endLabel != null) {
                instructions.bindLabel(endLabel)
            }
        }

        else {
            instructions.invokeConstructor(
                resolvedCallable.classDesc,
                resolvedCallable.methodTypeDesc
            ) {
                for (arg in args) {
                    arg.emit(this, environment)
                }
            }
        }
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val resolvedCallable = resolveCallable(environment)

        if (resolvedCallable.isFunction) {
            val returnType = resolvedCallable.methodTypeDesc.returnType()
            return DataType.of(returnType, resolvedCallable.isReturnTypeNullable)
        }

        else {
            return DataType.ofNonNull(resolvedCallable.classDesc)
        }
    }

    context(parents: ParentMap)
    private fun resolveFunction(environment: Environment): ResolvedCallable {
        val parent = parent

        val function = resolveMeazyFunction(environment)
            ?: error("Can't find function for " + id + " and args " + args)

        val className = function.environment.getParent().fullClassName
            ?: error("Invalid function's parent")

        val target = if (Modifiers.shared in function.modifiers || function.environment.getParent() is FileEnvironment) {
            null
        }
        else if (parent is MemberExpression) parent.receiver
        else ThisLiteral()

        val returnDataType = function.returnDataType

        return ResolvedCallable(
            ClassDesc.of(className),
            MethodTypeDesc.of(
                returnDataType?.classDesc ?: ConstantDescs.CD_void,
                function.parameters
                    .map { it.dataType.classDesc }.toList()
            ),
            returnDataType != null && returnDataType.isNullable,
            target,
            function.environment.getParent().run {
                this is ClassEnvironment && isInterface
            },
            true
        )
    }

    context(parents: ParentMap)
    private fun resolveMeazyFunction(environment: Environment): FunctionSymbol? {
        val parent = parent
        val args = args.map { it.getType(environment) }

        if (parent is MemberExpression && this == parent.member) {
            val classDesc = parent.receiver.getType(environment).classDesc
            val cls = environment.getClass(classDesc) ?: return null
            return cls.environment.getFunctionRecursively(id, args)
        }

        return environment.getFunction(id, args)
    }



    context(parents: ParentMap)
    private fun resolveConstructor(environment: Environment): ResolvedCallable {
        val constructorEnvironment = resolveMeazyConstructor(environment)
            ?: error("Can't find constructor for $id")

        val classEnvironment = constructorEnvironment.environment.getParent()
        if (classEnvironment !is ClassEnvironment) {
            throw RuntimeException("Invalid constructor")
        }

        if (classEnvironment.hasModifier(Modifiers.abstract)) {
            throw RuntimeException("Can't create instance of abstract class " + classEnvironment.id)
        }

        val parameters = constructorEnvironment.parameters.map { it.dataType.classDesc }.toList()

        return ResolvedCallable(
            ClassDesc.of(classEnvironment.fullClassName),
            MethodTypeDesc.of(ConstantDescs.CD_void, parameters),
            false,
            null,
            false,
            false
        )
    }

    context(parents: ParentMap)
    private fun resolveMeazyConstructor(environment: Environment): ConstructorSymbol? {
        val args = args.map { it.getType(environment) }

        val cls = environment.getClass(environment.resolveClassDesc(id, false)) ?: return null
        return cls.environment.getConstructor(args)
    }

    context(parents: ParentMap)
    private fun resolveCallable(environment: Environment): ResolvedCallable {
        val resolvedFunction = try {
            resolveFunction(environment)
        }
        catch (_: Exception) {
            null
        }

        val resolvedConstructor = try {
            resolveConstructor(environment)
        }
        catch (_: Exception) {
            null
        }

        val resolvedCallable: ResolvedCallable

        if (resolvedFunction != null) {
            if (resolvedConstructor != null) {
                error("Ambiguous call with id $id")
            }
            resolvedCallable = resolvedFunction
        }
        else {
            if (resolvedConstructor == null) {
                error("Can't find callable with id $id")
            }
            resolvedCallable = resolvedConstructor
        }

        return resolvedCallable
    }

    override fun alwaysReturns() = false



    private class ResolvedCallable(
        val classDesc: ClassDesc,
        val methodTypeDesc: MethodTypeDesc,
        val isReturnTypeNullable: Boolean,
        val target: Expression?,
        val isInterface: Boolean,
        val isFunction: Boolean
    )
}