package me.itzisonn_.meazy.parser.modifier;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.modifier.custom.*;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Modifiers registrar
 *
 * @see Registries#MODIFIERS
 */
public final class Modifiers {
    private static boolean hasRegistered = false;

    private Modifiers() {}



    public static Modifier PRIVATE() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("private")).getValue();
    }

    public static Modifier PROTECTED() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("protected")).getValue();
    }

    public static Modifier OPEN() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("open")).getValue();
    }

    public static Modifier SHARED() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("shared")).getValue();
    }

    public static Modifier ABSTRACT() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("abstract")).getValue();
    }

    public static Modifier GET() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("get")).getValue();
    }

    public static Modifier SET() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("set")).getValue();
    }

    public static Modifier DATA() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("data")).getValue();
    }

    public static Modifier OPERATOR() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("operator")).getValue();
    }

    public static Modifier ENUM() {
        return Registries.MODIFIERS.getEntry(MeazyMain.getDefaultIdentifier("enum")).getValue();
    }



    /**
     * Finds registered Modifier with given id
     *
     * @param id Id of Modifier
     * @return Modifier with given id or null
     */
    @Nullable
    public static Modifier parse(@NonNull String id) {
        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            if (id.equals(entry.getValue().getId())) return entry.getValue();
        }

        return null;
    }



    /**
     * Initializes {@link Registries#MODIFIERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#MODIFIERS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Modifiers have already been initialized");
        hasRegistered = true;

        register(new PrivateModifier());
        register(new ProtectedModifier());
        register(new OpenModifier());
        register(new SharedModifier());
        register(new AbstractModifier());
        register(new GetModifier());
        register(new SetModifier());
        register(new DataModifier());
        register(new OperatorModifier());
        register(new EnumModifier());
    }

    private static void register(Modifier modifier) {
        Registries.MODIFIERS.register(MeazyMain.getDefaultIdentifier(modifier.getId()), modifier);
    }
}
