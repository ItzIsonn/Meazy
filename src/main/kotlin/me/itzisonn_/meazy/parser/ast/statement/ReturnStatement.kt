package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.convertPrimitiveOrBoxed
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment
import me.itzisonn_.meazy.runtime.environment.getParentOrSelf
import me.itzisonn_.meazy.runtime.environment.hasParentOrSelf
import me.itzisonn_.meazy.runtime.environment.isInstanceOf

class ReturnStatement(val value: Expression?) : LocalStatement {
    override val children = let {
        if (value == null) setOf()
        else setOf(value)
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
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

        value.emit(instructions, environment)
        val valueClassDesc = value.getType(environment).classDesc
        val returnTypeClassDesc = returnDataType.classDesc

        if (!functionEnvironment.isInstanceOf(valueClassDesc, returnTypeClassDesc)) {
            if (!instructions.convertPrimitiveOrBoxed(valueClassDesc, returnTypeClassDesc)) {
                throw RuntimeException("Function's return value not matches its return data type TODO")
            }
        }

        instructions.returnValue(returnTypeClassDesc)
    }

    override fun alwaysReturns() = true
}
