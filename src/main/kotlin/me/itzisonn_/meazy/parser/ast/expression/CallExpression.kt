package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.SymbolResolver.resolveConstructor
import me.itzisonn_.meazy.parser.ast.SymbolResolver.resolveFunction
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.parent
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.*
import me.itzisonn_.meazy.util.tryOrNull
import java.lang.constant.ClassDesc
import java.lang.constant.MethodTypeDesc
import kotlin.uuid.Uuid

class CallExpression(
    val id: String,
    val args: List<Expression>
) : Expression, LocalStatement {
    override val children = args.toSet()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val resolvedCallable = environment.resolveCallable()

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
        val resolvedCallable = environment.resolveCallable()

        if (resolvedCallable.isFunction) {
            val returnType = resolvedCallable.methodTypeDesc.returnType()
            return DataType.of(returnType, resolvedCallable.isReturnTypeNullable)
        }

        else {
            return DataType.ofNonNull(resolvedCallable.classDesc)
        }
    }



    context(parents: ParentMap)
    private fun Environment.resolveFunction(): ResolvedCallable {
        val resolvedFunction = resolveFunction(this@CallExpression)

        return ResolvedCallable(
            resolvedFunction.classDesc,
            resolvedFunction.methodTypeDesc,
            resolvedFunction.isReturnTypeNullable,
            resolvedFunction.target,
            resolvedFunction.isInterface,
            true
        )
    }

    context(parents: ParentMap)
    private fun Environment.resolveConstructor(): ResolvedCallable {
        val classSymbol = getClass(resolveClassDesc(id, false))
            ?: error("Can't find class with id $id")
        val classEnvironment = classSymbol.environment

        val resolvedConstructor = resolveConstructor(classEnvironment, args)

        if (classEnvironment.hasModifier(Modifiers.abstract)) {
            throw RuntimeException("Can't create instance of abstract class " + classEnvironment.id)
        }

        return ResolvedCallable(
            resolvedConstructor.classDesc,
            resolvedConstructor.methodTypeDesc,
            false,
            null,
            isInterface = false,
            isFunction = false
        )
    }

    context(parents: ParentMap)
    private fun Environment.resolveCallable(): ResolvedCallable {
        val resolvedFunction = tryOrNull { resolveFunction() }
        val resolvedConstructor = tryOrNull { resolveConstructor() }

        val resolvedCallable: ResolvedCallable

        if (resolvedFunction != null) {
            if (resolvedConstructor != null) error("Ambiguous call with id $id")
            resolvedCallable = resolvedFunction
        }
        else {
            if (resolvedConstructor == null) error("Can't find callable with id $id")
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