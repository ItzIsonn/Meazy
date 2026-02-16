package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EnumModifier extends Modifier {
    public EnumModifier() {
        super("enum");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.ABSTRACT())) return false;

        return modifierStatement instanceof ClassDeclarationStatement;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
