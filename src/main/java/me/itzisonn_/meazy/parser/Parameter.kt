package me.itzisonn_.meazy.parser

/**
 * Represents parameter in callable statements
 *
 * @param id         Id
 * @param dataType   Data type
 * @param isConstant Whether this parameter is constant
 */
data class Parameter(
    val id: String,
    val dataType: DataType,
    val isConstant: Boolean
)