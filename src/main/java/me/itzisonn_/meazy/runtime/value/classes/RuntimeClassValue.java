package me.itzisonn_.meazy.runtime.value.classes;

import me.itzisonn_.meazy.parser.ast.Statement;

import java.util.List;

/**
 * Represents runtime class value
 */
public interface RuntimeClassValue extends ClassValue {
    /**
     * @return Body
     */
    List<Statement> getBody();
}
