package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.version.Version;

import java.util.List;
import java.util.Map;

/**
 * Represents compiled Meazy program
 */
@Getter
public class Program implements Statement {
    /**
     * Program's version
     */
    private final Version version;
    /**
     * Program's required addons
     */
    private final Map<String, Version> requiredAddons;
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
    public Program(Version version, Map<String, Version> requiredAddons, List<Statement> body) throws NullPointerException {
        if (version == null) throw new NullPointerException("Version can't be null");
        if (requiredAddons == null) throw new NullPointerException("RequiredAddons can't be null");
        if (body == null) throw new NullPointerException("Body can't be null");

        this.version = version;
        this.requiredAddons = requiredAddons;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder requiredAddonsBuilder = new StringBuilder();
        for (String addonId : requiredAddons.keySet()) {
            requiredAddonsBuilder.append(Statement.getOffset(offset)).append("require ").append(addonId);

            Version addonVersion = requiredAddons.get(addonId);
            if (addonVersion != null) requiredAddonsBuilder.append(" \"").append(addonVersion).append("\"");
            requiredAddonsBuilder.append("\n");
        }

        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            bodyBuilder.append(Statement.getOffset(offset)).append(body.get(i).toCodeString(offset + 1));
            if (i != body.size() - 1) bodyBuilder.append("\n");
        }

        return requiredAddonsBuilder + "\n" + bodyBuilder;
    }
}