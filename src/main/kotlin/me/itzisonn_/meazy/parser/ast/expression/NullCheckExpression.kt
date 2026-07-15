package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.boxPrimitive
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.instruction.boxed
import me.itzisonn_.meazy.parser.ast.ParentMap

class NullCheckExpression(
    val checkExpression: Expression,
    val nullExpression: Expression
) : Expression {
    override val children = setOf(checkExpression, nullExpression)

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val checkExpressionType = checkExpression.getType(environment)
        if (!checkExpressionType.isNullable) {
            checkExpression.emit(instructions, environment)
            return
        }

        val endLabel = instructions.createAndInitLabel()

        checkExpression.emit(instructions, environment)
        instructions.duplicate()
        instructions.gotoLabelIfNonNull(endLabel)

        instructions.pop()
        nullExpression.emit(instructions, environment)
        val nullExpressionClassDesc = nullExpression.getType(environment).classDesc
        if (nullExpressionClassDesc.isPrimitive) instructions.boxPrimitive(nullExpressionClassDesc)
        instructions.gotoLabel(endLabel)

        instructions.bindLabel(endLabel)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val checkExpressionType = checkExpression.getType(environment)
        if (!checkExpressionType.isNullable) return checkExpressionType

        val nullExpressionType = nullExpression.getType(environment)
        val nullExpressionClassDesc = nullExpressionType.classDesc.boxed

        return DataType.commonOf(
            environment,
            checkExpressionType.asNonNull(),
            nullExpressionType.with(nullExpressionClassDesc)
        )
    }
}
