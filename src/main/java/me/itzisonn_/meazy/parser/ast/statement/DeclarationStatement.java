package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface DeclarationStatement extends Statement {
    void declare(Environment environment);
    void resolve(Environment environment);
}
