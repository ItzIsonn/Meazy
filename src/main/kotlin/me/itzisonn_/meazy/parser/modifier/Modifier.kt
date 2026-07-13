package me.itzisonn_.meazy.parser.modifier

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Defines behavior of statements
 * @param id Id
 */
abstract class Modifier(val id: String) {
    /**
     * @param modifierStatement Modifier statement with this modifier
     * @param environment       Environment that has given modifierStatement in it
     * @return Whether this modifier can be used on the given modifierStatement in given environment
     */
    abstract fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean

    /**
     * @param requestEnvironment Environment from which asked an access
     * @param environment        Environment that has ModifierStatement in it
     * @param identifier         Identifier
     * @param hasModifier        Whether object with given identifier has this modifier
     * @return Whether object (variable, function, class, etc.) with [identifier] is accessible in [environment] from [requestEnvironment]
     */
    abstract fun canAccess(
        requestEnvironment: Environment,
        environment: Environment,
        identifier: Identifier,
        hasModifier: Boolean
    ): Boolean

    override fun toString() = id

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Modifier) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return 59 + id.hashCode()
    }
}