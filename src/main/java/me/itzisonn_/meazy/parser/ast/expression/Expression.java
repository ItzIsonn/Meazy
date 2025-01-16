package me.itzisonn_.meazy.parser.ast.expression;

import me.itzisonn_.meazy.parser.ast.statement.Statement;

/**
 * Expression represents the unit of the program with only one (not-full) line possible
 */
public interface Expression extends Statement {
    /**
     * @return String representation of this expression
     */
    String toCodeString();

    @Override
    default String toCodeString(int offset) {
        return toCodeString();
    }
}
