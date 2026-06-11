package me.itzisonn_.meazy.parser.operator

/**
 * Represents different types of operator
 */
enum class OperatorType {
    /**
     * Type of operator that is before an expression
     */
    PREFIX,

    /**
     * Type of operator that is between an expression
     */
    INFIX,

    /**
     * Type of operator that is after an expression
     */
    POSTFIX
}
