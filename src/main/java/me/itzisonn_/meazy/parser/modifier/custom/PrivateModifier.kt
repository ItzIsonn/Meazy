package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ConstructorClassIdentifier;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PrivateModifier extends Modifier {
    public PrivateModifier() {
        super("private");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.ABSTRACT()) || modifierStatement.getModifiers().contains(Modifiers.PROTECTED()) ||
                modifierStatement.getModifiers().contains(Modifiers.OPEN())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                modifierStatement instanceof ConstructorDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (!hasModifier) return true;

        if (identifier instanceof ConstructorClassIdentifier) {
            return EnvironmentUtils.hasParent(requestEnvironment, env -> {
                if (env instanceof ClassEnvironment classEnv) return classEnv.getId().equals(identifier.getId());
                return false;
            });
        }

        return requestEnvironment == environment || EnvironmentUtils.hasParent(requestEnvironment, environment);
    }
}
