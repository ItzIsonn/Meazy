package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

/**
 * EvaluationFunction
 *
 * @param <T> Type of Statement to evaluate
 *
 * @see Registries#EVALUATION_FUNCTIONS
 */
public interface EvaluationFunction<T extends Statement> {
    /**
     * Evaluates given statement using given environment
     *
     * @param object Statement to evaluate
     * @param environment Which Environment should the Statement be evaluated in
     * @param extra Extra info
     * @return Evaluated value
     */
    RuntimeValue<?> evaluate(T object, Environment environment, Object... extra);
}