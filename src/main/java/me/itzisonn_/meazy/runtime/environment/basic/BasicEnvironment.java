package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.function.Predicate;

public class BasicEnvironment implements Environment {
    @Getter
    protected final Environment parent;
    protected boolean isShared;

    public BasicEnvironment(Environment parent, boolean isShared) {
        this.parent = parent;
        this.isShared = isShared;
    }

    public BasicEnvironment(Environment parent) {
        this.parent = parent;
        this.isShared = false;
    }

    public boolean hasParent(Environment environment) {
        if (environment.equals(parent)) return true;
        if (parent != null) return parent.hasParent(environment);
        return false;
    }

    public boolean hasParent(Predicate<Environment> predicate) {
        if (predicate.test(parent)) return true;
        if (parent != null) return parent.hasParent(predicate);
        return false;
    }

    public VariableDeclarationEnvironment getVariableEnvironment(String id) {
        if (parent == null) throw new InvalidIdentifierException("Variable with id " + id + " doesn't exist!");
        return parent.getVariableEnvironment(id);
    }

    public FunctionDeclarationEnvironment getFunctionEnvironment(String id, ArrayList<RuntimeValue<?>> args) {
        if (parent == null) throw new InvalidIdentifierException("Function with id " + id + " doesn't exist!");
        return parent.getFunctionEnvironment(id, args);
    }

    public boolean isShared() {
        if (isShared) return true;
        return parent.isShared();
    }
}