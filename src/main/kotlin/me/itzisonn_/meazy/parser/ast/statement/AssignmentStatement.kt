package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ResolvedGlobalVariable
import me.itzisonn_.meazy.parser.ast.ResolvedLocalVariable
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.SymbolResolver.resolveVariable
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.isInstanceOf

class AssignmentStatement(val target: Expression, val value: Expression) : LocalStatement {
    override val children = setOf(target, value)

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val resolvedVariable = environment.resolveVariable(target)
        if (resolvedVariable.isConstant) throw RuntimeException("Can't reassign constant variable " + resolvedVariable.id + " TODO")

        val variableType = resolvedVariable.type
        val valueType = value.getType(environment).classDesc

        if (resolvedVariable is ResolvedGlobalVariable && resolvedVariable.receiver != null) {
            resolvedVariable.receiver.emit(instructions, environment)
        }

        value.emit(instructions, environment)

        if (!environment.isInstanceOf(valueType, variableType)) {
            if (!instructions.convertPrimitiveOrBoxed(valueType, variableType)) {
                throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
            }
        }

        when (resolvedVariable) {
            is ResolvedLocalVariable -> {
                instructions.storeLocal(resolvedVariable.type, resolvedVariable.slot)
            }

            is ResolvedGlobalVariable -> {
                if (resolvedVariable.receiver == null) {
                    instructions.storeStaticField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
                }
                else {
                    instructions.storeField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
                }
            }
        }
    }

    override fun alwaysReturns() = false
}
