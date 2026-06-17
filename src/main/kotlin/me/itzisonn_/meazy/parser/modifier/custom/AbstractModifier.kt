package me.itzisonn_.meazy.parser.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment

class AbstractModifier : Modifier("abstract") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.private in modifierStatement.modifiers || Modifiers.shared in modifierStatement.modifiers ||
            Modifiers.open in modifierStatement.modifiers || Modifiers.enum in modifierStatement.modifiers) return false

        if (modifierStatement is ClassDeclarationStatement) return true
        if (modifierStatement is FunctionDeclarationStatement && environment is ClassEnvironment) {
            return Modifiers.abstract in environment.modifiers
        }
        return false
    }

    override fun canAccess(
        requestEnvironment: Environment, environment: Environment,
        identifier: Identifier, hasModifier: Boolean
    ) = true
}
