package me.itzisonn_.meazy.parser.ast.expression;

import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents unit of the program with only one (not-full) line possible
 */
@NullMarked
public interface Expression extends Statement {
    DataType getType(Environment environment, Statement parent);
}
