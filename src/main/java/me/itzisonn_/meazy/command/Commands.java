package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.command.custom.*;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;

import java.io.*;

/**
 * Commands registrar
 *
 * @see Registries#COMMANDS
 */
public final class Commands {
    private static boolean hasRegistered = false;

    private Commands() {}



    /**
     * Finds registered AbstractCommand with given name
     *
     * @param name AbstractCommand's name
     * @return AbstractCommand with given name or null
     */
    public static AbstractCommand getByName(String name) {
        for (RegistryEntry<AbstractCommand> entry : Registries.COMMANDS.getEntries()) {
            if (entry.getValue().getName().equals(name)) return entry.getValue();
        }

        return null;
    }



    /**
     * Initializes {@link Registries#COMMANDS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#COMMANDS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Commands have already been initialized");
        hasRegistered = true;

        register(new VersionCommand());
        register(new AddonsCommand());
        register(new RunCommand());
        register(new CompileCommand());
        register(new DecompileCommand());
    }




    private static void register(AbstractCommand command) {
        Registries.COMMANDS.register(MeazyMain.getDefaultIdentifier(command.getName()), command);
    }
}
