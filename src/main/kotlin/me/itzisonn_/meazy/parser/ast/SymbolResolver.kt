package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ConstructorSymbol
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getFunction
import me.itzisonn_.meazy.runtime.environment.getVariable
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

object SymbolResolver {
    context(parents: ParentMap)
    fun Environment.resolveVariable(target: ProgramUnit): ResolvedVariable {
        val variableSymbol = resolveVariableSymbol(target) ?: error("Can't find variable")
        val className = variableSymbol.parentEnvironment.fullClassName

        val receiver = if (Modifiers.shared in variableSymbol.modifiers || variableSymbol.parentEnvironment is FileEnvironment) {
            null
        }
        else {
            val memberExpression = getMemberExpression(target)
            memberExpression?.receiver ?: ThisLiteral()
        }

        return ResolvedVariable(
            if (className == null) null else ClassDesc.of(className),
            if (className == null) variableSymbol.slot else -1,
            variableSymbol.isConstant,
            variableSymbol.id!!,
            variableSymbol.dataType.classDesc,
            variableSymbol.dataType.isNullable,
            receiver
        )
    }

    context(parents: ParentMap)
    private fun Environment.resolveVariableSymbol(target: ProgramUnit): VariableSymbol? {
        val memberExpression = getMemberExpression(target)
        if (memberExpression != null) {
            if (memberExpression.member !is Identifier) {
                error("Can't assign value to not variable TODO")
            }

            val classDesc = memberExpression.receiver.getType(this).classDesc
            val cls = getClass(classDesc) ?: return null
            return cls.environment.getVariable(memberExpression.member.id)
        }

        if (target is Identifier) {
            return getVariable(target.id)
        }

        error("Invalid target to resolve variable")
    }

    context(parents: ParentMap)
    private fun getMemberExpression(target: ProgramUnit): MemberExpression? {
        if (target is MemberExpression) {
            return target
        }

        val parent = target.parent
        if (parent is MemberExpression && parent.member == target) {
            return parent
        }

        return null
    }



    context(parents: ParentMap)
    fun Environment.resolveFunction(target: ProgramUnit): ResolvedFunction {
        val functionSymbol = resolveFunctionSymbol(target) ?: error("Can't find function")
        val className = functionSymbol.environment.getParent().fullClassName ?: error("Invalid function's parent")

        val target = if (Modifiers.shared in functionSymbol.modifiers || functionSymbol.environment.getParent() is FileEnvironment) {
            null
        }
        else {
            val memberExpression = getMemberExpression(target)
            memberExpression?.receiver ?: ThisLiteral()
        }

        val returnDataType = functionSymbol.returnDataType

        return ResolvedFunction(
            ClassDesc.of(className),
            MethodTypeDesc.of(
                returnDataType?.classDesc ?: ConstantDescs.CD_void,
                functionSymbol.parameters.map { it.dataType.classDesc }
            ),
            returnDataType != null && returnDataType.isNullable,
            target,
            functionSymbol.environment.getParent().run {
                this is ClassEnvironment && isInterface
            }
        )
    }

    context(parents: ParentMap)
    private fun Environment.resolveFunctionSymbol(target: ProgramUnit): FunctionSymbol? {
        val memberExpression = getMemberExpression(target)
        if (memberExpression != null) {
            if (memberExpression.member !is CallExpression) {
                error("Can't assign value to not variable TODO")
            }

            val classDesc = memberExpression.receiver.getType(this).classDesc
            val cls = getClass(classDesc) ?: return null
            return cls.environment.getFunctionRecursively(
                memberExpression.member.id,
                memberExpression.member.args.map { it.getType(this) }
            )
        }

        if (target is CallExpression) {
            return getFunction(target.id, target.args.map { it.getType(this) })
        }

        error("Invalid target to resolve function")
    }



    context(parents: ParentMap)
    fun Environment.resolveConstructor(classEnvironment: ClassEnvironment, args: List<Expression>): ResolvedConstructor {
        val constructorSymbol = resolveConstructorSymbol(classEnvironment, args)
            ?: error("Failed to resolve constructor")

        val parameters = constructorSymbol.parameters.map { it.dataType.classDesc }

        return ResolvedConstructor(
            ClassDesc.of(constructorSymbol.environment.getParent().fullClassName),
            MethodTypeDesc.of(ConstantDescs.CD_void, parameters)
        )
    }

    context(parents: ParentMap)
    fun Environment.resolveConstructorSymbol(classEnvironment: ClassEnvironment, args: List<Expression>): ConstructorSymbol? {
        val args = args.map { it.getType(this) }
        return classEnvironment.getConstructor(args)
    }
}



class ResolvedVariable(
    val classDesc: ClassDesc?,
    val slot: Int,
    val isConstant: Boolean,
    val id: String,
    val type: ClassDesc,
    val isNullable: Boolean,
    val receiver: Expression?
)

class ResolvedFunction(
    val classDesc: ClassDesc,
    val methodTypeDesc: MethodTypeDesc,
    val isReturnTypeNullable: Boolean,
    val target: Expression?,
    val isInterface: Boolean
)

class ResolvedConstructor(
    val classDesc: ClassDesc,
    val methodTypeDesc: MethodTypeDesc
)