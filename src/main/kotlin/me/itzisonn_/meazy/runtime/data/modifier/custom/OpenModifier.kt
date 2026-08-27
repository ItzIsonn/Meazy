package me.itzisonn_.meazy.runtime.data.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.areFromSamePackage

class OpenModifier : Modifier("open") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.private in modifierStatement.modifiers || Modifiers.protected in modifierStatement.modifiers) return false

        if (environment is FileEnvironment) {
            return modifierStatement is VariableDeclarationStatement || modifierStatement is FunctionDeclarationStatement ||
                    modifierStatement is ClassDeclarationStatement
        }

        if (environment is ClassEnvironment && Modifiers.open in environment.modifiers) {
            return modifierStatement is VariableDeclarationStatement || modifierStatement is FunctionDeclarationStatement ||
                    modifierStatement is ConstructorDeclarationStatement
        }

        return false
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ): Boolean {
        return hasModifier || areFromSamePackage(environment, requestEnvironment)
    }
}
