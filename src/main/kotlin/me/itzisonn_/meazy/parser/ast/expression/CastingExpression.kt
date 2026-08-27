package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc

class CastingExpression(
    val value: Expression,
    val id: String,
    val isSafe: Boolean
) : Expression {
    override val children = setOf(value)

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        value.emit(instructions, environment)

        val valueClassDesc = value.getType(environment).classDesc
        if (valueClassDesc.isPrimitive) instructions.boxPrimitive(valueClassDesc)

        val classDesc = environment.resolveClassDesc(id, false)

        if (isSafe) {
            val nullLabel = instructions.createAndInitLabel()
            val endLabel = instructions.createAndInitLabel()

            instructions.duplicate()
            instructions.instanceOf(classDesc)
            instructions.gotoLabelIfEqualsZero(nullLabel)

            instructions.cast(classDesc)
            instructions.gotoLabel(endLabel)

            instructions.bindLabel(nullLabel)
            instructions.pop()
            instructions.loadNull()
            instructions.cast(classDesc)

            instructions.bindLabel(endLabel)
        }
        else {
            instructions.cast(classDesc)
        }
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val classDesc = environment.resolveClassDesc(id, false)
        return DataType.of(classDesc, isSafe)
    }
}