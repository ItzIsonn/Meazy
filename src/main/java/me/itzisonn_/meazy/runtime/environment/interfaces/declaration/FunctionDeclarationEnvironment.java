package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.runtime.environment.RuntimeFunction;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;

public interface FunctionDeclarationEnvironment extends Environment {
    void declareFunction(RuntimeValue<?> value);
    RuntimeValue<?> getFunction(String id, ArrayList<RuntimeValue<?>> args);
    ArrayList<RuntimeFunction> getFunctions();
}