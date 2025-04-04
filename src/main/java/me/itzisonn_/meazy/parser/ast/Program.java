package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;

import java.util.List;

/**
 * Represents compiled Meazy program
 */
@Getter
public class Program implements Statement {
    /**
     * Program's version
     */
    private final String version;
    /**
     * Program's body
     */
    private final List<Statement> body;

    /**
     * Program constructor
     *
     * @param version Program's version
     * @param body Program's body
     *
     * @throws NullPointerException If either version or body is null
     */
    public Program(String version, List<Statement> body) throws NullPointerException {
        if (version == null) throw new NullPointerException("Version can't be null");
        if (body == null) throw new NullPointerException("Body can't be null");

        this.version = version;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            bodyBuilder.append(Statement.getOffset(offset)).append(body.get(i).toCodeString(offset + 1));
            if (i != body.size() - 1) bodyBuilder.append("\n");
        }

        return bodyBuilder.toString();
    }
}