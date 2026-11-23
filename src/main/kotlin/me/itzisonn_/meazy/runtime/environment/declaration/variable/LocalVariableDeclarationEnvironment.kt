package me.itzisonn_.meazy.runtime.environment.declaration.variable

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.symbol.LocalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment
import me.itzisonn_.meazy.runtime.environment.getParent
import me.itzisonn_.meazy.runtime.environment.hasParent
import me.itzisonn_.meazy.util.text.translatable
import java.lang.constant.ConstantDescs
import kotlin.uuid.Uuid

/**
 * Adds to Environment ability to declare local variables
 */
interface LocalVariableDeclarationEnvironment : VariableDeclarationEnvironment<LocalVariableSymbol> {
    fun declareShadowableVariable(id: String, type: DataType, value: Expression?): LocalVariableSymbol
    fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): LocalVariableSymbol

    //TODO
    fun getStartLabel(): Uuid?
    fun getEndLabel(): Uuid?

    fun setStartLabel(label: Uuid)
    fun setEndLabel(label: Uuid)

    val nextAvailableSlot: Int
}



open class LocalVariableDeclarationEnvironmentImpl(
    parent: Environment,
    private var startLabel: Uuid?,
    private var endLabel: Uuid?
) : EnvironmentImpl(parent), LocalVariableDeclarationEnvironment {
    private val _variables = mutableListOf<LocalVariableSymbol>()
    override val variables get() = _variables.toList()

    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): LocalVariableSymbol {
        getVariable(id)?.let { variable ->
            if (!variable.shadowable) throw EvaluationException(translatable("runtime.variable.already_exists", id))
        }

        val variableValue = LocalVariableSymbol(id, type, isConstant, modifiers, nextAvailableSlot, false, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun declareShadowableVariable(id: String, type: DataType, value: Expression?): LocalVariableSymbol {
        getVariable(id)?.let { variable ->
            if (!variable.shadowable) throw EvaluationException(translatable("runtime.variable.already_exists", id))
        }

        val variableValue = LocalVariableSymbol(id, type, true, setOf(), nextAvailableSlot, true, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun declareVariable(type: DataType, isConstant: Boolean, value: Expression?): LocalVariableSymbol {
        val variableValue = LocalVariableSymbol(null, type, isConstant, setOf(), nextAvailableSlot, false, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun getStartLabel() = startLabel
    override fun getEndLabel() = endLabel

    override fun setStartLabel(label: Uuid) { startLabel = label }
    override fun setEndLabel(label: Uuid) { endLabel = label }

    override val nextAvailableSlot: Int
        get() {
            var usedSlots = 0
            if (hasParent<ConstructorEnvironment>(true)) usedSlots++
            getParent<FunctionEnvironment>(true)?.let { functionEnvironment ->
                if (functionEnvironment.getParent() is ClassEnvironment) usedSlots++
            }

            for (variableValue in _variables) {
                val classDesc = variableValue.dataType.classDesc
                usedSlots += if (classDesc == ConstantDescs.CD_double || classDesc == ConstantDescs.CD_long) 2 else 1
            }

            return usedSlots + ((getParent() as? LocalVariableDeclarationEnvironment)?.nextAvailableSlot ?: 0)
        }
}