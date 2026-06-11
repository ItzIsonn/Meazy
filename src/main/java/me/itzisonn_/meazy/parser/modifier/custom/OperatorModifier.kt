package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OperatorModifier extends Modifier {
    public OperatorModifier() {
        super("operator");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.ABSTRACT()) || modifierStatement.getModifiers().contains(Modifiers.PRIVATE()) ||
                modifierStatement.getModifiers().contains(Modifiers.PROTECTED()) || modifierStatement.getModifiers().contains(Modifiers.SHARED())) return false;

        return modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
