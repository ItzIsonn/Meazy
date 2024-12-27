package me.itzisonn_.meazy.command;

import java.util.List;

public abstract class Command {
    private final List<String> args;

    public Command(List<String> args) {
        this.args = args;
    }

    public abstract String execute(String[] args);

    public List<String> getArgs() {
        return List.copyOf(args);
    }
}
