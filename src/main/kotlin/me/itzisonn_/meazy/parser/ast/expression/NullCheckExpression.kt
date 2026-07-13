package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.instruction.boxed

class NullCheckExpression(
    val checkExpression: Expression,
    val nullExpression: Expression
) : Expression {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val checkExpressionType = checkExpression.getType(environment, this)
        if (!checkExpressionType.isNullable) {
            checkExpression.emit(instructions, environment, this)
            return
        }

        val endLabel = instructions.createAndInitLabel()

        checkExpression.emit(instructions, environment, this)
        instructions.duplicate()
        instructions.gotoLabelIfNonNull(endLabel)

        instructions.pop()
        nullExpression.emit(instructions, environment, this)
        val nullExpressionClassDesc = nullExpression.getType(environment, this).classDesc
        if (nullExpressionClassDesc.isPrimitive) instructions.boxPrimitive(nullExpressionClassDesc)
        instructions.gotoLabel(endLabel)

        instructions.bindLabel(endLabel)
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val checkExpressionType = checkExpression.getType(environment, this)
        if (!checkExpressionType.isNullable) return checkExpressionType

        val nullExpressionType = nullExpression.getType(environment, this)
        val nullExpressionClassDesc = nullExpressionType.classDesc.boxed

        return DataType.commonOf(
            environment,
            checkExpressionType.asNonNull(),
            nullExpressionType.with(nullExpressionClassDesc)
        )
    }
}
