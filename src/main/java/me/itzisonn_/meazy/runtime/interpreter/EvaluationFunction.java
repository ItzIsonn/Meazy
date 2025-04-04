package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Represents function that is used to evaluate {@link Statement}s
 * @param <T> Type of Statement to evaluate
 * @see Registries#EVALUATION_FUNCTIONS
 */
public interface EvaluationFunction<T extends Statement> {
    /**
     * Evaluates given statement using given environment and extra info
     *
     * @param object Statement to evaluate
     * @param environment In which Environment should the Statement be evaluated
     * @param extra Extra info
     *
     * @return Evaluated value
     */
    RuntimeValue<?> evaluate(T object, Environment environment, Object... extra);
}