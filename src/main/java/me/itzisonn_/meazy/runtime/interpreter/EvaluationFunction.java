package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

public interface EvaluationFunction<T extends Statement> {
    RuntimeValue<?> evaluateStatement(T object, Environment environment, Object... extra);
}