package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OpenModifier extends Modifier {
    public OpenModifier() {
        super("open");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.PRIVATE()) || modifierStatement.getModifiers().contains(Modifiers.PROTECTED())) return false;

        if (environment instanceof FileEnvironment) {
            return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ClassDeclarationStatement;
        }

        if (environment instanceof ClassEnvironment classEnvironment && classEnvironment.getModifiers().contains(Modifiers.OPEN())) {
            return modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ConstructorDeclarationStatement;
        }

        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (hasModifier) return true;
        return EnvironmentUtils.areFromSamePackage(environment, requestEnvironment);
    }
}
