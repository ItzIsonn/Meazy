package me.itzisonn_.meazy.parser.ast.program;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.version.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Represents compiled Meazy program
 */
@NullMarked
public interface Program extends Statement {
    /**
     * File that contains this program
     */
    @Nullable
    File getFile();

    /**
     * Sets file of this program to given file
     * @param file File that contains this program
     *
     * @throws IllegalStateException If this program already have had file
     * @throws IllegalArgumentException If file doesn't exist or is a directory
     */
    void setFile(File file) throws IllegalStateException;

    /**
     * Version
     */
    Version getVersion();
    /**
     * Required addons
     */
    Map<String, @Nullable Version> getRequiredAddons();

    /**
     * Body
     */
    List<Statement> getBody();

    @Override
    void emit(InstructionsSet instructionsSet, Environment environment, @Nullable Statement parent);
}