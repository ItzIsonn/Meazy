package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.parser.ast.Statement;

public interface LocalBodyStatement extends Statement {
    boolean alwaysReturns();
}
