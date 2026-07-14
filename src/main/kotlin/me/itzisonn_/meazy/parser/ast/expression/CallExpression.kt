package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.*
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import kotlin.uuid.Uuid

class CallExpression(
    val caller: Identifier,
    val args: List<Expression>
) : Expression, LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val resolvedCallable = resolveCallable(environment, parent)

        if (resolvedCallable.isFunction) {
            var endLabel: Uuid? = null

            if (resolvedCallable.target != null) {
                resolvedCallable.target.emit(instructions, environment, this)

                if (parent is MemberExpression) {
                    if (!parent.isNullSafe) {
                        if (resolvedCallable.target.getType(environment, this).isNullable) {
                            throw RuntimeException("Unsafe member call of function " + caller.id + " on object of type " + resolvedCallable.classDesc.descriptorString())
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
                caller.id,
                resolvedCallable.methodTypeDesc,
                if (resolvedCallable.target == null)
                    if (resolvedCallable.isInterface) InvokeType.STATIC_INTERFACE else InvokeType.STATIC
                    else if (resolvedCallable.isInterface) InvokeType.INTERFACE else InvokeType.VIRTUAL
            ) {
                for (i in args.indices) {
                    val parameterType = resolvedCallable.methodTypeDesc.parameterType(i)

                    val arg = args[i]
                    val argType = arg.getType(environment, this@CallExpression).classDesc

                    arg.emit(this, environment, this@CallExpression)

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
                    arg.emit(this, environment, this@CallExpression)
                }
            }
        }
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val resolvedCallable = resolveCallable(environment, parent)

        if (resolvedCallable.isFunction) {
            val returnType = resolvedCallable.methodTypeDesc.returnType()
            return DataType.of(returnType, resolvedCallable.isReturnTypeNullable)
        }

        else {
            return DataType.ofNonNull(resolvedCallable.classDesc)
        }
    }

    private fun resolveFunction(environment: Environment, parent: ProgramUnit): ResolvedCallable {
        val functionEnvironment = resolveMeazyFunction(environment, parent)
            ?: error("Can't find function for " + caller.id + " and args " + args)

        val className = functionEnvironment.getParent().fullClassName
            ?: error("Invalid function's parent")

        val target = if (Modifiers.shared in functionEnvironment.modifiers || functionEnvironment.getParent() is FileEnvironment) {
            null
        }
        else if (parent is MemberExpression) parent.receiver
        else ThisLiteral()

        val returnDataType = functionEnvironment.returnDataType

        return ResolvedCallable(
            ClassDesc.of(className),
            MethodTypeDesc.of(
                returnDataType?.classDesc ?: ConstantDescs.CD_void,
                functionEnvironment.parameters
                    .map { it.dataType.classDesc }.toList()
            ),
            returnDataType != null && returnDataType.isNullable,
            target,
            functionEnvironment.getParent().run {
                this is ClassEnvironment && isInterface
            },
            true
        )
    }

    private fun resolveMeazyFunction(environment: Environment, parent: ProgramUnit): FunctionEnvironment? {
        val id = caller.id
        val args = args.stream().map<DataType> { arg: Expression? -> arg!!.getType(environment, this) }.toList()

        if (parent is MemberExpression) {
            val classDesc = parent.receiver.getType(environment, this).classDesc
            val classEnvironment = environment.getClass(classDesc) ?: return null
            return classEnvironment.getFunctionRecursively(id, args)
        }

        return environment.getFunction(id, args)
    }


    private fun resolveConstructor(environment: Environment): ResolvedCallable {
        val constructorEnvironment = resolveMeazyConstructor(environment)
            ?: error("Can't find constructor for " + caller.id)

        val classEnvironment = constructorEnvironment.getParent()
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

    private fun resolveMeazyConstructor(environment: Environment): ConstructorEnvironment? {
        val id = caller.id
        val args = args.map { it.getType(environment, this) }

        val classEnvironment = environment.getClass(environment.resolveClassDesc(id, false)) ?: return null
        return classEnvironment.getConstructor(args)
    }

    private fun resolveCallable(environment: Environment, parent: ProgramUnit): ResolvedCallable {
        val resolvedFunction = try {
            resolveFunction(environment, parent)
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
                error("Ambiguous call with id ${caller.id}")
            }
            resolvedCallable = resolvedFunction
        }
        else {
            if (resolvedConstructor == null) {
                error("Can't find callable with id ${caller.id}")
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