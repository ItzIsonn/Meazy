package me.itzisonn_.meazy.parser.pasing_function;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ParsingFunction;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public abstract class AbstractParsingFunction<T extends ProgramUnit> implements ParsingFunction<T> {
    private final String id;

    protected AbstractParsingFunction(String id) {
        this.id = id;
    }
}
