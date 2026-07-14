package me.itzisonn_.meazy.runtime.data.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment

class SharedModifier : Modifier("shared") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.abstract in modifierStatement.modifiers) return false

        if (modifierStatement is VariableDeclarationStatement || modifierStatement is FunctionDeclarationStatement) {
            return environment is ClassEnvironment
        }
        return false
    }

    override fun canAccess(
        requestEnvironment: Environment,
        environment: Environment,
        identifier: Identifier,
        hasModifier: Boolean
    ): Boolean {
        if (hasModifier) return true

        if (identifier is VariableIdentifier) {
            if (environment !is VariableDeclarationEnvironment) return true
            if (environment.getVariable(identifier.id) == null) return true

            return !environment.isShared || environment is FileEnvironment || environment is GlobalEnvironment
        }

        return true
    }
}
