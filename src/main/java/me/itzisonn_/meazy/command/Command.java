package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.registry.Registries;

import java.util.List;

/**
 * Command
 *
 * @see Registries#COMMANDS
 */
public abstract class Command {
    /**
     * List of args' names
     */
    private final List<String> args;

    /**
     * Command constructor
     *
     * @param args Args' names
     *
     * @throws NullPointerException If given args is null
     * @throws IllegalArgumentException If any of given args doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public Command(List<String> args) throws NullPointerException, IllegalArgumentException {
        if (args == null) throw new NullPointerException("Arg's can't be null");
        if (args.stream().allMatch(arg -> arg.matches(Utils.IDENTIFIER_REGEX))) throw new IllegalArgumentException("Invalid arg's name");
        this.args = args;
    }

    /**
     * Executes this command with given args.
     * Args' amount matches {@link Command#args}'s size
     *
     * @param args Args
     * @return Success message that will be logged or null
     */
    public abstract String execute(String[] args);

    /**
     * @return Copy of command's args' names
     */
    public List<String> getArgs() {
        return List.copyOf(args);
    }
}
