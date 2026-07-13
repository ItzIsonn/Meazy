package me.itzisonn_.meazy.runtime.data.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment

class OperatorModifier : Modifier("operator") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.abstract in modifierStatement.modifiers || Modifiers.private in modifierStatement.modifiers ||
            Modifiers.protected in modifierStatement.modifiers || Modifiers.shared in modifierStatement.modifiers) {
            return false
        }

        return modifierStatement is FunctionDeclarationStatement && environment is ClassEnvironment
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ) = true
}
