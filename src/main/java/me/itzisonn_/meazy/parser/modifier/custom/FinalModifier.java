package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FinalModifier extends Modifier {
    public FinalModifier() {
        super("final");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.ABSTRACT())) return false;

        if (modifierStatement instanceof ClassDeclarationStatement) return true;
        return modifierStatement instanceof FunctionDeclarationStatement && environment instanceof ClassEnvironment;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
