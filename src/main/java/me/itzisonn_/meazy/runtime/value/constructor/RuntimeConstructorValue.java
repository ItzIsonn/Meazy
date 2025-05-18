package me.itzisonn_.meazy.runtime.value.constructor;

import me.itzisonn_.meazy.parser.ast.Statement;

import java.util.List;

/**
 * Represents runtime constructor value
 */
public interface RuntimeConstructorValue extends ConstructorValue {
    /**
     * @return Body
     */
    List<Statement> getBody();
}
