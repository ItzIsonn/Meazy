package me.itzisonn_.meazy.runtime.value.classes;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.Set;

/**
 * Represents class value
 */
public interface ClassValue extends RuntimeValue<Object> {
    /**
     * @return Base classes
     */
    Set<String> getBaseClasses();

    /**
     * @return Environment
     */
    ClassEnvironment getEnvironment();



    /**
     * @param value Value to check
     * @return Whether given value matches this class value
     */
    boolean isMatches(Object value);

    /**
     * @param value Value to check
     * @return Whether given value matches this class value or it's base classes
     */
    boolean isLikeMatches(Object value);



    /**
     * @return Id of this class's environment
     */
    String getId();

    /**
     * @return All modifiers of this class's environment
     */
    Set<Modifier> getModifiers();
}
