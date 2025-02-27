package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.VariableValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.ConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.FunctionValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements ClassEnvironment {
    @Getter
    protected final String id;
    protected final List<ConstructorValue> constructors;
    protected final Set<ClassEnvironment> baseClasses;
    protected final Set<Modifier> modifiers;

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, String id, Set<Modifier> modifiers) {
        super(parent, isShared);
        this.id = id;
        this.constructors = new ArrayList<>();
        this.baseClasses = new HashSet<>();
        this.modifiers = modifiers;
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, String id) {
        this(parent, isShared, id, new HashSet<>());
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, String id, Set<Modifier> modifiers) {
        this(parent, false, id, modifiers);
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, String id) {
        this(parent, false, id);
    }

    @Override
    public void declareVariable(VariableValue value) {
        if (getVariable(value.getId()) != null) throw new InvalidSyntaxException("Variable with id " + value.getId() + " already exists!");
        variables.add(value);
    }

    @Override
    public VariableValue getVariable(String id) {
        VariableValue variableValue = super.getVariable(id);
        if (variableValue != null) return variableValue;

        for (ClassEnvironment baseClass : baseClasses) {
            VariableValue variable = baseClass.getVariable(id);
            if (variable != null) return variable;
        }

        return null;
    }

    @Override
    public Environment getVariableDeclarationEnvironment(String id) {
        if (super.getVariable(id) != null) return this;

        for (ClassEnvironment baseClass : baseClasses) {
            VariableValue variable = baseClass.getVariable(id);
            if (variable != null) return baseClass;
        }

        return super.getVariableDeclarationEnvironment(id);
    }

    @Override
    public FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
        FunctionValue functionValue = super.getFunction(id, args);
        if (functionValue != null) return functionValue;

        for (ClassEnvironment baseClass : baseClasses) {
            FunctionValue function = baseClass.getFunction(id, args);
            if (function != null) return function;
        }

        return null;
    }

    @Override
    public FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        if (super.getFunction(id, args) != null) return this;

        for (ClassEnvironment baseClass : baseClasses) {
            FunctionValue function = baseClass.getFunction(id, args);
            if (function != null) return baseClass;
        }

        return super.getFunctionDeclarationEnvironment(id, args);
    }

    @Override
    public void declareConstructor(ConstructorValue value) {
        List<CallArgExpression> args = value.getArgs();

        main:
        for (ConstructorValue constructorValue : constructors) {
            List<CallArgExpression> callArgExpressions = constructorValue.getArgs();

            if (args.size() != callArgExpressions.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                CallArgExpression callArgExpression = callArgExpressions.get(i);
                if (!callArgExpression.getDataType().equals(args.get(i).getDataType())) continue main;
            }

            throw new InvalidSyntaxException("Constructor with this args already exists!");
        }

        constructors.add(value);
    }

    @Override
    public List<ConstructorValue> getConstructors() {
        return new ArrayList<>(constructors);
    }

    @Override
    public void addBaseClass(ClassEnvironment classEnvironment) {
        baseClasses.add(classEnvironment);
    }

    @Override
    public Set<ClassEnvironment> getBaseClasses() {
        return new HashSet<>(baseClasses);
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}