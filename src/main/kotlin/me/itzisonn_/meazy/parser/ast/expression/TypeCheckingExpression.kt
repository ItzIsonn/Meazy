package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ConstantDescs

class TypeCheckingExpression(
    val value: Expression,
    val id: String
) : Expression {
    override val children = setOf(value)

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        value.emit(instructions, environment)

        val valueClassDesc = value.getType(environment).classDesc
        if (valueClassDesc.isPrimitive) instructions.boxPrimitive(valueClassDesc)

        val classDesc = environment.resolveClassDesc(id, false)
        instructions.instanceOf(classDesc)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}