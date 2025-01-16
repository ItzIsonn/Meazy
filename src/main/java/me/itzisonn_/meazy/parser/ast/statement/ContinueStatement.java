package me.itzisonn_.meazy.parser.ast.statement;

public class ContinueStatement implements Statement {
    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        return "continue";
    }
}
