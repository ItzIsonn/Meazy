package me.itzisonn_.meazy.runtime.data.modifier.custom

import me.itzisonn_.meazy.parser.ast.expression.identifier.ConstructorClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getParent
import me.itzisonn_.meazy.runtime.environment.hasParent
import me.itzisonn_.meazy.util.text.translatable

class ProtectedModifier : Modifier("protected") {
    override fun canUse(modifierStatement: ModifierStatement, environment: Environment): Boolean {
        if (Modifiers.private in modifierStatement.modifiers || Modifiers.open in modifierStatement.modifiers) return false

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

        return when (identifier) {
            is VariableIdentifier -> (requestEnvironment === environment || requestEnvironment.hasParent(environment) ||
                    requestEnvironment.hasParent { env ->
                        if (env is ClassEnvironment) {
                            val declarationEnvironment = environment.getParent<ClassEnvironment>() ?: return@hasParent false
                            if (env.id == declarationEnvironment.id) return@hasParent true

                            val parentClassEnvironment = environment.getClass(env.id)
                                ?: throw EvaluationException(
                                    translatable("meazy:runtime.class.doesnt_exist", env.id)
                                )

                            return@hasParent parentClassEnvironment.interfaces
                                .any { it.displayName() == declarationEnvironment.id }
                        }
                        false
                    })

            is FunctionIdentifier -> (requestEnvironment === environment || requestEnvironment.hasParent(environment) ||
                    requestEnvironment.hasParent { parentEnv ->
                        if (parentEnv is ClassEnvironment) {
                            val declarationEnvironment = environment as? ClassEnvironment
                                ?: environment.getParent<ClassEnvironment>()

                            if (declarationEnvironment == null) return@hasParent false
                            if (parentEnv.id == declarationEnvironment.id) return@hasParent true

                            val parentClassEnvironment =
                                environment.getClass(parentEnv.id) ?: throw EvaluationException(
                                    translatable(
                                        "meazy:runtime.class.doesnt_exist",
                                        parentEnv.id
                                    )
                                )

                            return@hasParent parentClassEnvironment.interfaces
                                .any { it.displayName() == declarationEnvironment.id }
                        }
                        false
                    })

            is ConstructorClassIdentifier -> requestEnvironment.hasParent { env ->
                if (env is ClassEnvironment) {
                    if (env.id == identifier.id) return@hasParent true

                    val parentClassEnvironment = requestEnvironment.getClass(env.id)
                        ?: throw EvaluationException(translatable("meazy:runtime.class.doesnt_exist", env.id))

                    return@hasParent parentClassEnvironment.interfaces.any { it.displayName() == identifier.id }
                }
                false
            }

            else -> true
        }
    }
}
