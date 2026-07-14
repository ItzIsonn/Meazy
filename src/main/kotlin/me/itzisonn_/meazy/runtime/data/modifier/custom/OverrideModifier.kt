package me.itzisonn_.meazy.runtime.data.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.Environment

class OverrideModifier : Modifier("override") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        return modifierStatement is FunctionDeclarationStatement
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ) = true
}
