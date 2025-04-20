package me.itzisonn_.meazy.command;

import lombok.Getter;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;

import java.util.List;

/**
 * Represents command
 * @see Registries#COMMANDS
 */
public abstract class Command {
    /**
     * Name
     */
    @Getter
    private final String name;
    /**
     * List of args' names
     */
    private final List<String> args;

    /**
     * @param name Name
     * @param args List of args' names
     *
     * @throws NullPointerException If either name or args is null
     * @throws IllegalArgumentException If either name or any of args doesn't match {@link MeazyMain#IDENTIFIER_REGEX}
     */
    public Command(String name, List<String> args) throws NullPointerException, IllegalArgumentException {
        if (name == null) throw new NullPointerException("Name can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        if (!name.matches(MeazyMain.IDENTIFIER_REGEX)) throw new IllegalArgumentException("Invalid command's name");
        if (!args.isEmpty() && args.stream().allMatch(arg -> arg.matches(MeazyMain.IDENTIFIER_REGEX))) throw new IllegalArgumentException("Invalid arg's name");

        this.name = name;
        this.args = args;
    }

    /**
     * Executes this command with given args.
     * Args' amount matches {@link Command#args}' size
     *
     * @param args Args
     * @return Success message that will be logged or null
     */
    public abstract String execute(String... args);

    /**
     * @return Copy of args' names
     */
    public List<String> getArgs() {
        return List.copyOf(args);
    }
}
