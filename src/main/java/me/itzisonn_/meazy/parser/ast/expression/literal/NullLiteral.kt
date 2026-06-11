package me.itzisonn_.meazy.parser.ast.expression.literal

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class NullLiteral : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        instructions.loadNull()
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return DataType.ofNullable(ConstantDescs.CD_Object)
    }
}
