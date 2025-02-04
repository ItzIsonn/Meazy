package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;

/**
 * All basic Modifiers
 *
 * @see Registries#MODIFIERS
 */
public final class Modifiers {
    private static boolean isInit = false;

    private Modifiers() {}



    public static Modifier PRIVATE() {
        return Registries.MODIFIERS.getEntry(RegistryIdentifier.ofDefault("private")).getValue();
    }

    public static Modifier SHARED() {
        return Registries.MODIFIERS.getEntry(RegistryIdentifier.ofDefault("shared")).getValue();
    }


    /**
     * Finds registered AccessModifier with given id
     *
     * @param id Id of AccessModifier
     * @return AccessModifier with given id or null
     */
    public static Modifier parse(String id) {
        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            if (id.equals(entry.getValue().getId())) return entry.getValue();
        }

        return null;
    }



    private static void register(Modifier modifier) {
        Registries.MODIFIERS.register(RegistryIdentifier.ofDefault(modifier.getId()), modifier);
    }

    /**
     * Initializes {@link Registries#MODIFIERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#MODIFIERS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("AccessModifiers have already been initialized!");
        isInit = true;

        register(new Modifier("private") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement ||
                    modifierStatement instanceof ConstructorDeclarationStatement) {
                    return environment instanceof ClassEnvironment;
                }
                return false;
            }
        });

        register(new Modifier("shared") {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                if (modifierStatement instanceof VariableDeclarationStatement || modifierStatement instanceof FunctionDeclarationStatement) {
                    return environment instanceof ClassEnvironment;
                }
                return false;
            }
        });
    }
}
