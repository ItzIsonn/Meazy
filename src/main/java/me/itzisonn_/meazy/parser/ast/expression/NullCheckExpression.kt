package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.util.MiscUtils.boxPrimitive
import me.itzisonn_.meazy.util.MiscUtils.getBoxedType

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
        if (nullExpressionClassDesc.isPrimitive) boxPrimitive(instructions, nullExpressionClassDesc)
        instructions.gotoLabel(endLabel)

        instructions.bindLabel(endLabel)
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val checkExpressionType = checkExpression.getType(environment, this)
        if (!checkExpressionType.isNullable) return checkExpressionType

        val nullExpressionType = nullExpression.getType(environment, this)
        var nullExpressionClassDesc = nullExpressionType.classDesc
        if (nullExpressionClassDesc.isPrimitive) nullExpressionClassDesc = getBoxedType(nullExpressionClassDesc)

        return DataType.commonOf(
            environment,
            checkExpressionType.asNonNull(),
            nullExpressionType.with(nullExpressionClassDesc)
        )
    }
}
