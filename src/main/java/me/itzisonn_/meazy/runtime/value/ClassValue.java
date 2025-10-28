package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;

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
     * @param fileEnvironment File environment
     * @param value Value to check
     * @return Whether given value matches this class value or it's base classes
     */
    boolean isLikeMatches(FileEnvironment fileEnvironment, Object value);



    /**
     * @return Id of this class's environment
     */
    String getId();

    /**
     * @return All modifiers of this class's environment
     */
    Set<Modifier> getModifiers();



    /**
     * Setups given class environment
     * @param context Runtime context
     * @param classEnvironment Class environment
     */
    void setupEnvironment(RuntimeContext context, ClassEnvironment classEnvironment);

    /**
     * Creates new instance of this class value
     *
     * @param classEnvironment Class environment
     * @return New instance of this class value
     */
    ClassValue newInstance(ClassEnvironment classEnvironment);
}
