package me.itzisonn_.meazy.runtime.value.function;

import me.itzisonn_.meazy.parser.ast.Statement;

import java.util.List;

/**
 * Represents runtime function value
 */
public interface RuntimeFunctionValue extends FunctionValue {
    /**
     * @return Body
     */
    List<Statement> getBody();
}
