package me.itzisonn_.meazy.parser.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.ConstructorClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.hasParent

class PrivateModifier : Modifier("private") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.abstract in modifierStatement.modifiers || Modifiers.protected in modifierStatement.modifiers ||
            Modifiers.open in modifierStatement.modifiers) return false

        if (modifierStatement is VariableDeclarationStatement || modifierStatement is FunctionDeclarationStatement ||
            modifierStatement is ConstructorDeclarationStatement) {
            return environment is ClassEnvironment
        }
        return false
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ): Boolean {
        if (!hasModifier) return true

        if (identifier is ConstructorClassIdentifier) {
            return requestEnvironment.hasParent { env ->
                if (env is ClassEnvironment) return@hasParent env.id == identifier.id
                false
            }
        }

        return requestEnvironment === environment || requestEnvironment.hasParent(environment)
    }
}
