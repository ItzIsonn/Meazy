package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.*
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.getClass
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.isInstanceOf
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.resolveClassDesc
import me.itzisonn_.meazy.util.MiscUtils.convertPrimitiveOrBoxed
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import kotlin.uuid.Uuid

class CallExpression(
    val caller: Identifier,
    val args: List<Expression>
) : Expression, LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        if (caller is FunctionIdentifier) {
            val resolvedFunction = resolveFunction(environment, parent)
            var endLabel: Uuid? = null

            if (resolvedFunction.target != null) {
                resolvedFunction.target.emit(instructions, environment, this)

                if (parent is MemberExpression) {
                    if (!parent.isNullSafe) {
                        if (resolvedFunction.target.getType(environment, this).isNullable) {
                            throw RuntimeException("Unsafe member call of function " + caller.getId() + " on object of type " + resolvedFunction.classDesc.descriptorString())
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
                resolvedFunction.classDesc,
                caller.getId(),
                resolvedFunction.methodTypeDesc,
                if (resolvedFunction.target == null)
                    if (resolvedFunction.isInterface) InvokeType.STATIC_INTERFACE else InvokeType.STATIC
                    else if (resolvedFunction.isInterface) InvokeType.INTERFACE else InvokeType.VIRTUAL
            ) {
                for (i in args.indices) {
                    val parameterType = resolvedFunction.methodTypeDesc.parameterType(i)

                    val arg = args[i]
                    val argType = arg.getType(environment, this@CallExpression).classDesc

                    arg.emit(this, environment, this@CallExpression)

                    if (!isInstanceOf(environment, argType, parameterType)) {
                        if (!convertPrimitiveOrBoxed(instructions, argType, parameterType)) {
                            throw RuntimeException("Can't pass argument of type $argType to parameter of type $parameterType")
                        }
                    }
                }
            }

            if (endLabel != null) {
                instructions.bindLabel(endLabel)
            }
        }
        else if (caller is ClassIdentifier) {
            val resolvedConstructor = resolveConstructor(environment)

            instructions.invokeConstructor(
                resolvedConstructor.classDesc,
                resolvedConstructor.methodTypeDesc
            ) {
                for (arg in args) {
                    arg.emit(this, environment, this@CallExpression)
                }
            }
        }
        else throw RuntimeException("Unknown caller TODO " + caller.javaClass.getName())
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        if (caller is FunctionIdentifier) {
            val function = resolveFunction(environment, parent)
            val returnType = function.methodTypeDesc.returnType()
            return DataType.of(returnType, function.isReturnTypeNullable)
        }

        if (caller is ClassIdentifier) {
            return DataType.ofNonNull(resolveConstructor(environment).classDesc)
        }

        throw RuntimeException("Unknown caller TODO" + caller::class.qualifiedName)
    }

    private fun resolveFunction(environment: Environment, parent: ProgramUnit): ResolvedCallable {
        val functionEnvironment = resolveMeazyFunction(environment, parent)
            ?: error("Can't find function for " + caller.getId() + " and args " + args)

        val className = functionEnvironment.getParent().fullClassName
            ?: error("Invalid function's parent")

        val target = if (parent is MemberExpression) {
            if (parent.receiver is ClassIdentifier) null else parent.receiver
        }
        else if (functionEnvironment.modifiers.contains(Modifiers.SHARED()) || functionEnvironment.getParent() is FileEnvironment) {
            null
        }
        else {
            ThisLiteral()
        }

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
            }
        )
    }

    private fun resolveMeazyFunction(environment: Environment, parent: ProgramUnit): FunctionEnvironment? {
        val id = caller.getId()
        val args = args.stream().map<DataType> { arg: Expression? -> arg!!.getType(environment, this) }.toList()

        if (parent is MemberExpression) {
            val classDesc = parent.receiver.getType(environment, this).classDesc

            val classEnvironment = getClass(environment, classDesc).orElse(null)
                ?: return null

            return classEnvironment.getFunctionRecursively(id, args).orElse(null)
        }

        return environment.getFunction(id, args)
    }


    private fun resolveConstructor(environment: Environment): ResolvedCallable {
        val constructorEnvironment = resolveMeazyConstructor(environment)
            ?: error("Can't find constructor for " + caller.getId())

        val classEnvironment = constructorEnvironment.getParent()
        if (classEnvironment !is ClassEnvironment) {
            throw RuntimeException("Invalid constructor")
        }

        if (classEnvironment.hasModifier(Modifiers.ABSTRACT())) {
            throw RuntimeException("Can't create instance of abstract class " + classEnvironment.id)
        }

        val parameters = constructorEnvironment.parameters.map { it.dataType.classDesc }.toList()

        return ResolvedCallable(
            ClassDesc.of(classEnvironment.fullClassName),
            MethodTypeDesc.of(ConstantDescs.CD_void, parameters),
            false,
            null,
            false
        )
    }

    private fun resolveMeazyConstructor(environment: Environment): ConstructorEnvironment? {
        val id = caller.getId()
        val args = args.stream().map<DataType> { arg: Expression? -> arg!!.getType(environment, this) }.toList()

        val classEnvironment =
            getClass(environment, resolveClassDesc(environment, id, false)).orElse(null) ?: return null

        return classEnvironment.getConstructor(args).orElse(null)
    }

    override fun alwaysReturns(): Boolean {
        return false
    }


    private class ResolvedCallable(
        val classDesc: ClassDesc,
        val methodTypeDesc: MethodTypeDesc,
        val isReturnTypeNullable: Boolean,
        val target: Expression?,
        val isInterface: Boolean
    )
}