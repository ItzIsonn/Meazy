package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.version.Version;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Represents compiled Meazy program
 */
@Getter
public class Program implements Statement {
    /**
     * File that contains this program
     */
    private final File file;
    /**
     * Version
     */
    private final Version version;
    /**
     * Required addons
     */
    private final Map<String, Version> requiredAddons;
    /**
     * Body
     */
    private final List<Statement> body;

    /**
     * @param file File that contains this program
     * @param version Version
     * @param requiredAddons Required addons
     * @param body Body
     *
     * @throws NullPointerException If either file, version, requiredAddons or body is null
     * @throws IllegalArgumentException If file doesn't exist or is a directory
     */
    public Program(File file, Version version, Map<String, Version> requiredAddons, List<Statement> body) throws NullPointerException, IllegalArgumentException {
        if (file == null) throw new NullPointerException("File can't be null");
        if (!file.exists()) throw new IllegalArgumentException("File doesn't exist");
        if (file.isDirectory()) throw new IllegalArgumentException("File can't be directory");

        if (version == null) throw new NullPointerException("Version can't be null");
        if (requiredAddons == null) throw new NullPointerException("RequiredAddons can't be null");
        if (body == null) throw new NullPointerException("Body can't be null");

        this.file = file;
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