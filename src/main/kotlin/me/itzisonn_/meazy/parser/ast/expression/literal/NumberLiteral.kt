package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class NumberLiteral(val value: String) : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val int = value.toIntOrNull()
        if (int != null) {
            instructions.loadConstant(int)
            return
        }

        val double = value.toDoubleOrNull() ?: error("Invalid number $value")
        instructions.loadConstant(double)
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val classDesc = when {
            value.toIntOrNull() != null -> ConstantDescs.CD_int
            value.toDoubleOrNull() != null -> ConstantDescs.CD_double
            else -> error("Invalid number $value")
        }

        return DataType.ofNonNull(classDesc)
    }
}
