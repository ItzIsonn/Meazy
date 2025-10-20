package me.itzisonn_.meazy.runtime.interpreter;

import lombok.Getter;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.value.*;

/**
 * Is used to evaluate statements
 * @see Registries#EVALUATION_FUNCTIONS
 */
@Getter
public class Interpreter {
    private final RuntimeContext context;
    /**
     * Output of the program
     */
    private final StringBuilder output = new StringBuilder();

    public Interpreter(RuntimeContext context) {
        this.context = context;
    }

    /**
     * Evaluates given statement using given environment and extra info
     *
     * @param statement Statement to evaluate
     * @param environment In which Environment should the Statement be evaluated
     * @param extra Extra info
     *
     * @return Evaluated value
     * @throws NullPointerException If either statement or environment is null
     */
    @SuppressWarnings("unchecked")
    public RuntimeValue<?> evaluate(Statement statement, Environment environment, Object... extra) throws NullPointerException {
        if (statement == null) throw new NullPointerException("Statement can't be null");
        if (environment == null) throw new NullPointerException("Environment can't be null");

        EvaluationFunction<Statement> evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(statement.getClass());
        Class<? extends Statement> parent = statement.getClass();

        while (evaluationFunction == null) {
            if (!Statement.class.isAssignableFrom(parent.getSuperclass())) {
                throw new IllegalArgumentException("Can't find EvaluationFunction, that evaluates statement with class " + statement.getClass().getName());
            }

            parent = (Class<? extends Statement>) parent.getSuperclass();
            evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(parent);
        }

        return evaluationFunction.evaluate(statement, context, environment, extra);
    }

    /**
     * Finds EvaluationFunction that corresponds to given class
     *
     * @param cls Class as key
     * @return EvaluationFunction or null
     *
     * @throws NullPointerException If given cls is null
     */
    private EvaluationFunction<? extends Statement> getEvaluationFunctionOrNull(Class<? extends Statement> cls) throws NullPointerException {
        if (cls == null) throw new NullPointerException("Cls can't be null");

        RegistryEntry<Pair<Class<? extends Statement>, EvaluationFunction<? extends Statement>>> entry = Registries.EVALUATION_FUNCTIONS.getEntryByKey(cls);
        if (entry == null) return null;

        return entry.getValue().getValue();
    }
}