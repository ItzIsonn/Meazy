package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment
import java.lang.constant.ConstantDescs
import kotlin.uuid.Uuid

class IfStatement(
    val cases: List<IfStatementCase>
) : LocalStatement {
    override val children = cases.flatMap { it.children } .toSet()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val endLabel = instructions.createAndInitLabel()

        for (case in cases) {
            case.emit(instructions, environment, endLabel)
        }

        instructions.bindLabel(endLabel)
    }

    override fun alwaysReturns(): Boolean {
        for (case in cases) {
            if (!case.alwaysReturns()) return false
        }

        return cases.last().condition == null
    }
}



class IfStatementCase(
    val condition: Expression?,
    val body: List<LocalStatement>
) {
    val children = body.toMutableSet<ProgramUnit>().apply {
        if (condition != null) add(condition)
    }.toSet()

    context(parents: ParentMap)
    fun emit(instructions: InstructionsSet, environment: Environment, endLabel: Uuid) {
        val startLabel = instructions.createAndInitLabel()
        val elseLabel = instructions.createAndInitLabel()
        val ifEnvironment = LocalVariableDeclarationEnvironment(
            environment, startLabel, elseLabel
        )

        if (condition == null) {
            instructions.bindLabel(startLabel)

            for (statement in body) {
                statement.emit(instructions, ifEnvironment)
            }

            instructions.bindLabel(elseLabel)
            return
        }

        val conditionType = condition.getType(environment)
        if (conditionType.isNullable || (conditionType.classDesc != ConstantDescs.CD_boolean && conditionType.classDesc != ConstantDescs.CD_Boolean)) {
            throw RuntimeException(
                "If statement must always use boolean TODO but uses " + condition.getType(
                    environment
                )
            )
        }

        condition.emit(instructions, environment)
        instructions.convertToBooleanType(conditionType.classDesc == ConstantDescs.CD_Boolean, false)
        instructions.gotoLabelIfEqualsZero(elseLabel)

        instructions.bindLabel(startLabel)
        for (statement in body) {
            statement.emit(instructions, ifEnvironment)
        }
        instructions.gotoLabel(endLabel)

        instructions.bindLabel(elseLabel)
    }

    fun alwaysReturns(): Boolean {
        for (localStatement in body) {
            if (localStatement.alwaysReturns()) return true
        }

        return false
    }
}