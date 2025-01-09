package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.*;

/**
 * Interpreter used to evaluate statements
 *
 * @see Registries#EVALUATION_FUNCTIONS
 */
public final class Interpreter {
    private Interpreter() {}

    /**
     * Evaluates given statement using given environment
     *
     * @param statement Statement to evaluate
     * @param environment Which Environment should the Statement be evaluated in
     * @param extra Extra info
     * @return Evaluated value
     *
     * @throws NullPointerException When given statement or environment is null
     */
    @SuppressWarnings("unchecked")
    public static RuntimeValue<?> evaluate(Statement statement, Environment environment, Object... extra) throws NullPointerException {
        if (statement == null || environment == null) throw new NullPointerException("Neither statement nor environment can't be null!");

        EvaluationFunction<Statement> evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(statement.getClass());
        Class<? extends Statement> parent = statement.getClass();
        while (evaluationFunction == null) {
            if (Statement.class.isAssignableFrom(parent.getSuperclass())) {
                parent = (Class<? extends Statement>) parent.getSuperclass();
            }
            else throw new RuntimeException("Can't find EvaluationFunction that evaluates statement with class " + statement.getClass().getName());
            evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(parent);
        }

        return evaluationFunction.evaluate(statement, environment, extra);
    }

    /**
     * Finds EvaluationFunction that corresponds to given class
     *
     * @param cls Class as key
     * @return EvaluationFunction or null
     */
    private static EvaluationFunction<? extends Statement> getEvaluationFunctionOrNull(Class<? extends Statement> cls) {
        RegistryEntry<Pair<Class<? extends Statement>, EvaluationFunction<? extends Statement>>> entry = Registries.EVALUATION_FUNCTIONS.getEntryByKey(cls);
        if (entry == null) return null;
        return entry.getValue().getValue();
    }
}