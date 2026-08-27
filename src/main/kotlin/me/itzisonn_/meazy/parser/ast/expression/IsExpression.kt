package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ConstantDescs

class IsExpression(
    val value: Expression,
    val dataType: String
) : Expression {
    override val children = setOf(value)

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val classDesc = environment.resolveClassDesc(dataType, false)
        val valueClassDesc = value.getType(environment).classDesc

        value.emit(instructions, environment)
        if (valueClassDesc.isPrimitive) instructions.boxPrimitive(valueClassDesc)

        instructions.instanceOf(classDesc)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}