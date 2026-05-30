package me.itzisonn_.meazy.parser.ast.expression;

import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents unit of the program with only one (not-full) line possible
 */
@NullMarked
public interface Expression extends ProgramUnit {
    DataType getType(Environment environment, ProgramUnit parent);
}
