package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment
import java.lang.constant.ConstantDescs

class WhileStatement(
    val condition: Expression,
    val body: List<LocalStatement>
) : LocalStatement {
    override val children = body.toMutableSet<ProgramUnit>().apply {
        add(condition)
    }.toSet()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        if (condition.getType(environment) != DataType.ofNonNull(ConstantDescs.CD_boolean)) {
            throw RuntimeException("While statement must always use boolean TODO")
        }

        val conditionLabel = instructions.createAndInitLabel()
        val endLabel = instructions.createAndInitLabel()
        val loopEnvironment = LoopEnvironment(environment, conditionLabel, endLabel)

        instructions.bindLabel(conditionLabel)
        condition.emit(instructions, environment)
        instructions.gotoLabelIfEqualsZero(endLabel)

        for (statement in body) {
            statement.emit(instructions, loopEnvironment)
        }

        instructions.gotoLabel(conditionLabel)
        instructions.bindLabel(endLabel)
    }

    override fun alwaysReturns(): Boolean {
        for (localStatement in body) {
            if (localStatement.alwaysReturns()) return true
        }

        return false
    }
}
