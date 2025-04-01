package me.itzisonn_.meazy.parser.ast;

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



    /**
     * Returns offset represented by a string
     *
     * @param offset Number of offsets
     * @return String offset
     *
     * @throws IllegalArgumentException When given offset is negative
     */
    static String getOffset(int offset) throws IllegalArgumentException {
        if (offset < 0) throw new IllegalArgumentException("Offset can't be negative");

        return "\t".repeat(offset);
    }
}