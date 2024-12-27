package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.parser.ast.statement.Statement;

public interface ParsingFunction<T extends Statement> {
    T parse(Object... extra);
}