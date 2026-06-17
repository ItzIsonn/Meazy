package me.itzisonn_.meazy.parser.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment

class GetModifier : Modifier("get") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        return modifierStatement is VariableDeclarationStatement &&
                environment is ClassEnvironment
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ) = true
}
