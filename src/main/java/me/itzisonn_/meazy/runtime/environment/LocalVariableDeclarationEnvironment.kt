package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.VariableValue
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