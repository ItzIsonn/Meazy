package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.registry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.*;

public final class BasicInterpreter {
    private BasicInterpreter() {}

    @SuppressWarnings("unchecked")
    public static RuntimeValue<?> evaluate(Statement statement, Environment environment, Environment... extra) {
        if (statement == null) throw new NullPointerException("Statement can't be null!");

        EvaluationFunction<Statement> evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(statement.getClass());
        Class<? extends Statement> parent = statement.getClass();
        while (evaluationFunction == null) {
            if (Statement.class.isAssignableFrom(parent.getSuperclass())) {
                parent = (Class<? extends Statement>) parent.getSuperclass();
            }
            else throw new UnsupportedNodeException(statement.getClass().getName());
            evaluationFunction = (EvaluationFunction<Statement>) getEvaluationFunctionOrNull(parent);
        }

        return evaluationFunction.evaluateStatement(statement, environment, extra);
    }

    private static EvaluationFunction<? extends Statement> getEvaluationFunctionOrNull(Class<? extends Statement> cls) {
        RegistryEntry<Pair<Class<? extends Statement>, EvaluationFunction<? extends Statement>>> entry = Registries.EVALUATION_FUNCTION.getEntry(cls);
        if (entry == null) return null;
        return entry.getValue().getValue();
    }
}