package me.itzisonn_.meazy.runtime.environment.declaration.variable

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.symbol.LocalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.util.text.translatable
import java.lang.constant.ConstantDescs
import kotlin.uuid.Uuid

/**
 * Adds to Environment ability to declare local variables
 */
interface LocalVariableDeclarationEnvironment : VariableDeclarationEnvironment<LocalVariableSymbol> {
    fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): LocalVariableSymbol

    //TODO
    fun getStartLabel(): Uuid?
    fun getEndLabel(): Uuid?

    fun setStartLabel(label: Uuid)
    fun setEndLabel(label: Uuid)

    val usedSlotsCount: Int
}



open class LocalVariableDeclarationEnvironmentImpl(
    parent: Environment,
    private var startLabel: Uuid?,
    private var endLabel: Uuid?
) : EnvironmentImpl(parent), LocalVariableDeclarationEnvironment {
    private val _variables = mutableListOf<LocalVariableSymbol>()
    override val variables get() = _variables.toList()

    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): LocalVariableSymbol {
        if (getVariable(id) != null) {
            throw EvaluationException(translatable("runtime.variable.already_exists", id))
        }

        var slot = usedSlotsCount
        if (!isShared) slot++

        var parentEnvironment: Environment? = getParent()
        while (parentEnvironment is LocalVariableDeclarationEnvironment) {
            slot += parentEnvironment.usedSlotsCount
            parentEnvironment = parentEnvironment.getParent()
        }

        val variableValue = LocalVariableSymbol(id, type, isConstant, modifiers, slot, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): LocalVariableSymbol {
        var slot = usedSlotsCount
        if (!isShared) slot++

        var parentEnvironment: Environment? = getParent()
        while (parentEnvironment is LocalVariableDeclarationEnvironment) {
            slot += parentEnvironment.usedSlotsCount
            parentEnvironment = parentEnvironment.getParent()
        }

        val variableValue = LocalVariableSymbol(null, type, isConstant, setOf(), slot, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun getStartLabel() = startLabel
    override fun getEndLabel() = endLabel

    override fun setStartLabel(label: Uuid) { startLabel = label }
    override fun setEndLabel(label: Uuid) { endLabel = label }

    override val usedSlotsCount: Int
        get() {
            var usedSlots = 0

            for (variableValue in _variables) {
                val classDesc = variableValue.dataType.classDesc
                usedSlots += if (classDesc == ConstantDescs.CD_double || classDesc == ConstantDescs.CD_long) 2 else 1
            }

            return usedSlots
        }

    override val isShared get() = getParent().isShared
}