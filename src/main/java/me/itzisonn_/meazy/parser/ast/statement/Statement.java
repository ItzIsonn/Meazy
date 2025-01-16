package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.parser.ast.expression.Expression;

/**
 * Statement represents the unit of the program with multiple lines possible
 *
 * @see Expression
 */
public interface Statement {
    /**
     * @param offset Non-negative offset that is added in front of string representation on each line
     * @return String representation of this statement
     *
     * @throws IllegalArgumentException If given offset is negative
     */
    String toCodeString(int offset) throws IllegalArgumentException;
}