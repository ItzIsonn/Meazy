package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OverrideModifier extends Modifier {
    public OverrideModifier() {
        super("override");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        return modifierStatement instanceof FunctionDeclarationStatement;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
