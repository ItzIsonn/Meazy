package me.itzisonn_.meazy.runtime.value.function;

import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;

/**
 * Represents native function value
 */
public interface NativeFunctionValue extends FunctionValue {
    /**
     * Runs this function with given args and environment
     *
     * @param functionArgs Args given to this function
     * @param functionEnvironment Unique Environment of this function
     */
    RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment);
}
