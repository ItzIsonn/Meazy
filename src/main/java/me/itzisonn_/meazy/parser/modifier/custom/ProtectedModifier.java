package me.itzisonn_.meazy.parser.modifier.custom;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ConstructorClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ProtectedModifier extends Modifier {
    public ProtectedModifier() {
        super("protected");
    }

    @Override
    public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
        if (modifierStatement.getModifiers().contains(Modifiers.PRIVATE()) || modifierStatement.getModifiers().contains(Modifiers.OPEN())) return false;

        if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                modifierStatement instanceof ConstructorDeclarationStatement) {
            return environment instanceof ClassEnvironment;
        }
        return false;
    }

    @Override
    public boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier) {
        if (!hasModifier) return true;

        return switch (identifier) {
            case VariableIdentifier _ ->
                    requestEnvironment == environment || EnvironmentUtils.hasParent(requestEnvironment, environment) ||
                            EnvironmentUtils.hasParent(requestEnvironment, env -> {
                                if (env instanceof ClassEnvironment classEnvironment) {
                                    ClassEnvironment declarationEnvironment = EnvironmentUtils.getParent(environment, ClassEnvironment.class).orElse(null);
                                    if (declarationEnvironment == null) return false;
                                    if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                                    ClassValue parentClassValue = EnvironmentUtils.getClassValue(environment, classEnvironment.getId()).orElse(null);
                                    if (parentClassValue == null) {
                                        throw new InvalidIdentifierException(Text.translatable("meazy:runtime.class.doesnt_exist", classEnvironment.getId()));
                                    }
                                    return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                                }

                                return false;
                            });

            case FunctionIdentifier _ ->
                    requestEnvironment == environment || EnvironmentUtils.hasParent(requestEnvironment, environment) ||
                            EnvironmentUtils.hasParent(requestEnvironment, parentEnv -> {
                                if (parentEnv instanceof ClassEnvironment classEnvironment) {
                                    ClassEnvironment declarationEnvironment;
                                    if (environment instanceof ClassEnvironment env) declarationEnvironment = env;
                                    else declarationEnvironment = EnvironmentUtils.getParent(environment, ClassEnvironment.class).orElse(null);

                                    if (declarationEnvironment == null) return false;
                                    if (classEnvironment.getId().equals(declarationEnvironment.getId())) return true;

                                    ClassValue parentClassValue = EnvironmentUtils.getClassValue(environment, classEnvironment.getId()).orElse(null);
                                    if (parentClassValue == null) {
                                        throw new InvalidIdentifierException(Text.translatable("meazy:runtime.class.doesnt_exist", classEnvironment.getId()));
                                    }
                                    return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(declarationEnvironment.getId()));
                                }

                                return false;
                            });

            case ConstructorClassIdentifier _ -> EnvironmentUtils.hasParent(requestEnvironment, env -> {
                if (env instanceof ClassEnvironment classEnvironment) {
                    if (classEnvironment.getId().equals(identifier.getId())) return true;

                    ClassValue parentClassValue = EnvironmentUtils.getClassValue(requestEnvironment, classEnvironment.getId()).orElse(null);
                    if (parentClassValue == null) {
                        throw new InvalidIdentifierException(Text.translatable("meazy:runtime.class.doesnt_exist", classEnvironment.getId()));
                    }
                    return parentClassValue.getBaseClasses().stream().anyMatch(cls -> cls.equals(identifier.getId()));
                }

                return false;
            });

            default -> true;
        };

    }
}
