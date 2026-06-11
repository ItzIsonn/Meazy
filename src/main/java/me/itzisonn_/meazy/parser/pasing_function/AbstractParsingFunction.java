package me.itzisonn_.meazy.parser.pasing_function;

import me.itzisonn_.meazy.parser.ParsingFunction;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AbstractParsingFunction<T extends ProgramUnit> implements ParsingFunction<T> {
    private final String id;

    protected AbstractParsingFunction(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
