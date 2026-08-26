package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class NumberLiteral(val value: String) : Expression {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val int = value.toIntOrNull()
        if (int != null) {
            instructions.loadConstant(int)
            return
        }

        val double = value.toDoubleOrNull() ?: error("Invalid number $value")
        instructions.loadConstant(double)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val classDesc = when {
            value.toIntOrNull() != null -> ConstantDescs.CD_int
            value.toDoubleOrNull() != null -> ConstantDescs.CD_double
            else -> error("Invalid number $value")
        }

        return DataType.ofNonNull(classDesc)
    }
}
