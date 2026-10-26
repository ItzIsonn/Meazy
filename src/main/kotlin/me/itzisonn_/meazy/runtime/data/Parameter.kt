package me.itzisonn_.meazy.runtime.data

/**
 * Represents parameter in callable statements
 *
 * @param id         Id
 * @param dataType   Data type
 */
data class Parameter(
    val id: String,
    val dataType: DataType
)