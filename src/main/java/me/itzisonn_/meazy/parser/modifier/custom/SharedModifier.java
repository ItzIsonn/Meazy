package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SharedModifier extends Modifier {
    public SharedModifier() {
        super("shared");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.ABSTRACT())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (hasModifier) return true;

        if (identifier instanceof VariableIdentifier) {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) return true;
            if (variableDeclarationEnvironment.getVariable(identifier.getId()).isEmpty()) return true;

            return !environment.isShared() || environment instanceof FileEnvironment || environment instanceof GlobalEnvironment;
        }

        if (identifier instanceof FunctionIdentifier) {
            return !environment.isShared() || environment instanceof FileEnvironment || environment instanceof GlobalEnvironment;
        }

        return true;
    }
}
