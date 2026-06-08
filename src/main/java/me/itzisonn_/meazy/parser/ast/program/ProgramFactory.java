package me.itzisonn_.meazy.parser.ast.program;

import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.version.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

//TODO javadoc
@NullMarked
public interface ProgramFactory {
    /**
     * Main constructor TODO
     *
     * @param file File that contains this program or null
     * @param version Version
     * @param body Body
     *
     * @throws IllegalArgumentException If file doesn't exist or is a directory
     */
    Program create(@Nullable File file, Version version, List<Statement> body) throws IllegalArgumentException;

    /**
     * Constructor with file set to null TODO
     *
     * @param version Version
     * @param body Body
     */
    Program create(Version version, List<Statement> body);
}
