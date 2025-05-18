package me.itzisonn_.meazy.runtime.value.constructor;

import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;

/**
 * Represents native constructor value
 */
public interface NativeConstructorValue extends ConstructorValue {
    /**
     * Runs this constructor with given args and environment
     *
     * @param constructorArgs Args given to this constructor
     * @param constructorEnvironment Unique Environment of this constructor
     */
    void run(List<RuntimeValue<?>> constructorArgs, ConstructorEnvironment constructorEnvironment);
}