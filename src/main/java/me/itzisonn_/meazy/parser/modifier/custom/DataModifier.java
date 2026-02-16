package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DataModifier extends Modifier {
    public DataModifier() {
        super("data");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        return modifierStatement instanceof ClassDeclarationStatement;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
