package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.variable_value.VariableValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.variable_value.VariableValueImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.*;

@NullMarked
public class ClassEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements ClassEnvironment {
    @Getter
    protected final String id;
    @Getter
    protected final boolean isInterface;
    protected final List<VariableValue> variables;
    protected final Set<ConstructorEnvironment> constructors;
    @Getter
    @Nullable
    protected ClassDesc baseClass;
    protected final Set<ClassDesc> interfaces;
    protected final Set<String> unresolvedBaseClasses;
    protected final Set<Modifier> modifiers;
    protected final Set<FunctionEnvironment> operatorFunctions;

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, @Nullable ClassDesc baseClass, Set<ClassDesc> interfaces, Set<Modifier> modifiers) {
        super(parent, isShared);
        this.id = id;
        this.isInterface = isInterface;
        variables = new ArrayList<>();
        constructors = new HashSet<>();
        this.baseClass = baseClass;
        this.interfaces = interfaces;
        unresolvedBaseClasses = new HashSet<>();
        this.modifiers = modifiers;
        operatorFunctions = new HashSet<>();
    }

    public ClassEnvironmentImpl(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, Set<String> unresolvedBaseClasses, Set<Modifier> modifiers) {
        super(parent, isShared);
        this.id = id;
        this.isInterface = isInterface;
        variables = new ArrayList<>();
        constructors = new HashSet<>();
        baseClass = null;
        interfaces = new HashSet<>();
        this.unresolvedBaseClasses = unresolvedBaseClasses;
        this.modifiers = modifiers;
        operatorFunctions = new HashSet<>();
    }



    @Override
    public ClassDeclarationEnvironment getParent() {
        return (ClassDeclarationEnvironment) parent;
    }

    @Override
    public Set<ClassDesc> getInterfaces() {
        return new HashSet<>(interfaces);
    }

    @Override
    public void resolveBaseClasses() {
        for (String unresolvedBaseClass : unresolvedBaseClasses) {
            ClassDesc classDesc = EnvironmentUtils.resolveClassDesc(parent, unresolvedBaseClass, false);
            ClassEnvironment baseClassEnvironment = EnvironmentUtils.getClassEnvironment(parent, classDesc).orElseThrow();

            if (baseClassEnvironment.isInterface()) interfaces.add(baseClassEnvironment.getClassDesc());
            else {
                if (baseClass != null) throw new RuntimeException("Class can't have more than one base class TODO");
                baseClass = baseClassEnvironment.getClassDesc();
            }
        }

        if (baseClass == null && !isInterface) baseClass = ConstantDescs.CD_Object;
        unresolvedBaseClasses.clear();
    }



    @Override
    public VariableValue declareVariable(String id, DataType dataType, boolean isConstant, @Nullable Expression value) {
        if (getVariable(id).isPresent()) {
            throw new EvaluationException(Text.translatable("meazy:runtime.variable.already_exists", id));
        }

        VariableValue variableValue = new VariableValueImpl(id, dataType, isConstant, Set.of(), variables.size(), value, this);
        variables.add(variableValue);
        return variableValue;
    }

    @Override
    public Optional<VariableValue> getVariable(String id) {
        Optional<VariableValue> variableValue = ClassEnvironment.super.getVariable(id);
        if (variableValue.isPresent()) return variableValue;

        if (baseClass == null) return Optional.empty();
        ClassEnvironment baseClassEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass.displayName()).orElse(null);

        if (baseClassEnvironment != null) return baseClassEnvironment.getVariable(id);
        return Optional.empty();
    }

    @Override
    public List<VariableValue> getVariables() {
        return new ArrayList<>(variables);
    }



    @Override
    public void declareOperatorFunction(FunctionEnvironment functionEnvironment) {
        List<ParameterExpression> parameters = functionEnvironment.getParameters();

        main:
        for (FunctionEnvironment otherFunctionEnvironment : operatorFunctions) {
            if (otherFunctionEnvironment.getId().equals(functionEnvironment.getId())) {
                List<ParameterExpression> otherParameters = otherFunctionEnvironment.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new EvaluationException(Text.translatable("meazy:runtime.function.operator.already_exists", functionEnvironment.getId()));
            }
        }

        operatorFunctions.add(functionEnvironment);
    }

    @Override
    public Set<FunctionEnvironment> getOperatorFunctions() {
        return new HashSet<>(operatorFunctions);
    }



    @Override
    public Optional<FunctionEnvironment> getFunction(String id, List<DataType> args) {
        Optional<FunctionEnvironment> functionEnvironment = super.getFunction(id, args);
        if (functionEnvironment.isPresent()) return functionEnvironment;

        if (baseClass == null) return Optional.empty();
        ClassEnvironment baseClassEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass).orElse(null);

        if (baseClassEnvironment != null) return baseClassEnvironment.getFunction(id, args);
        return Optional.empty();
    }



    @Override
    public void declareConstructor(ConstructorEnvironment constructorEnvironment) {
        List<ParameterExpression> parameters = constructorEnvironment.getParameters();

        main:
        for (ConstructorEnvironment otherConstructorEnvironment : constructors) {
            List<ParameterExpression> otherParameters = otherConstructorEnvironment.getParameters();
            if (parameters.size() != otherParameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
            }

            throw new EvaluationException(Text.translatable("meazy:runtime.constructor.already_exists"));
        }

        constructors.add(constructorEnvironment);
    }

    @Override
    public Set<ConstructorEnvironment> getConstructors() {
        return new HashSet<>(constructors);
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }

    @Override
    public String getFullClassName() {
        String classSpecifier;
        if (modifiers.contains(Modifiers.PRIVATE())) classSpecifier = EnvironmentUtils.getClassName(this).orElseThrow() + "$";
        else classSpecifier = "";

        return EnvironmentUtils.getPackageName(this).orElseThrow() + "." + classSpecifier + id;
    }

    @Override
    public ClassDesc getClassDesc() {
        return ClassDesc.of(getFullClassName());
    }
}