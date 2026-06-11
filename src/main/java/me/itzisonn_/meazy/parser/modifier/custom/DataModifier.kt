package me.itzisonn_.meazy.parser.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.Environment

class DataModifier : Modifier("data") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        return modifierStatement is ClassDeclarationStatement
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ) = true
}
