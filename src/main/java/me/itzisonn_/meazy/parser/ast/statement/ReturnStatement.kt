package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment
import me.itzisonn_.meazy.runtime.environment.getParentOrSelf
import me.itzisonn_.meazy.runtime.environment.hasParentOrSelf
import me.itzisonn_.meazy.runtime.environment.isInstanceOf
import me.itzisonn_.meazy.util.MiscUtils.convertPrimitiveOrBoxed

class ReturnStatement(val value: Expression?) : LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        if (environment.hasParentOrSelf<ConstructorEnvironment>()) {
            if (value != null) throw RuntimeException("Constructor can't return value TODO")
            instructions.returnVoid()
            return
        }

        val functionEnvironment = environment.getParentOrSelf<FunctionEnvironment>()
            ?: error("Parent environment for RETURN statement must be FunctionEnvironment TODO")

        val returnDataType = functionEnvironment.returnDataType

        if (value == null) {
            if (returnDataType != null) {
                throw RuntimeException("Function must return value TODO")
            }

            instructions.returnVoid()
            return
        }

        if (returnDataType == null) {
            throw RuntimeException("Function must not return value TODO")
        }

        value.emit(instructions, environment, this)
        val valueClassDesc = value.getType(environment, this).classDesc
        val returnTypeClassDesc = returnDataType.classDesc

        if (!functionEnvironment.isInstanceOf(valueClassDesc, returnTypeClassDesc)) {
            if (!convertPrimitiveOrBoxed(instructions, valueClassDesc, returnTypeClassDesc)) {
                throw RuntimeException("Function's return value not matches its return data type TODO")
            }
        }

        instructions.returnValue(returnTypeClassDesc)
    }

    override fun alwaysReturns() = true
}
