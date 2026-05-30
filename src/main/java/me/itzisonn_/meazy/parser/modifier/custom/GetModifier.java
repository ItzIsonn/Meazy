package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GetModifier extends Modifier {
    public GetModifier() {
        super("get");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        return modifierStatement instanceof VariableDeclarationStatement && environment instanceof ClassEnvironment;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        return true;
    }
}
