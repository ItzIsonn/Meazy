package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class BooleanLiteral(val value: Boolean) : Expression {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        instructions.loadConstant(value)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
