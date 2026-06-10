package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.text.translatable
import java.lang.constant.ConstantDescs
import kotlin.uuid.Uuid

/**
 * Adds to Environment ability to declare local variables
 */
interface LocalVariableDeclarationEnvironment : VariableDeclarationEnvironment {
    fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): VariableValue

    //TODO
    var startLabel: Uuid?
    var endLabel: Uuid?
    val usedSlotsCount: Int
}



open class LocalVariableDeclarationEnvironmentImpl(
    parent: Environment,
    override var startLabel: Uuid?,
    override var endLabel: Uuid?
) : VariableDeclarationEnvironmentImpl(parent), LocalVariableDeclarationEnvironment {
    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?): VariableValue {
        if (getVariable(id).isPresent) {
            throw EvaluationException(translatable("meazy:runtime.variable.already_exists", id))
        }

        var slot = usedSlotsCount
        if (!isShared) slot++

        var parentEnvironment: Environment? = parent
        while (parentEnvironment is LocalVariableDeclarationEnvironment) {
            slot += parentEnvironment.usedSlotsCount
            parentEnvironment = parentEnvironment.parent
        }

        val variableValue = VariableValue(id, type, isConstant, setOf(), slot, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): VariableValue {
        var slot = usedSlotsCount
        if (!isShared) slot++

        var parentEnvironment: Environment? = parent
        while (parentEnvironment is LocalVariableDeclarationEnvironment) {
            slot += parentEnvironment.usedSlotsCount
            parentEnvironment = parentEnvironment.parent
        }

        val variableValue = VariableValue(null, type, isConstant, setOf(), slot, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override val usedSlotsCount: Int
        get() {
            var usedSlots = 0

            for (variableValue in _variables) {
                val classDesc = variableValue.dataType.getClassDesc()

                usedSlots += if (classDesc == ConstantDescs.CD_double || classDesc == ConstantDescs.CD_long) 2 else 1
            }

            return usedSlots
        }
}