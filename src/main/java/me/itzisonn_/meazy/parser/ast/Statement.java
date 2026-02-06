package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents unit of the program with multiple lines possible
 *
 * @see Expression
 */
@NullMarked
public interface Statement { //TODO JAVADOC
    void emit(InstructionsSet instructionsSet, Environment environment, Statement parent);
}