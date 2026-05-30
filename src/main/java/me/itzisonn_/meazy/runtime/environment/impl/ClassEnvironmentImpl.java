package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.value.impl.ConstructorValueImpl;
import me.itzisonn_.meazy.runtime.value.impl.VariableValueImpl;
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
    protected final Set<ConstructorValue> constructors;
    @Getter
    @Nullable
    protected ClassDesc baseClass;
    protected final Set<ClassDesc> interfaces;
    protected final Set<String> unresolvedBaseClasses;
    protected final Set<Modifier> modifiers;
    protected final Set<FunctionValue> operatorFunctions;

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
            ClassValue baseClassValue = EnvironmentUtils.getClassValue(parent, classDesc).orElseThrow();

            if (baseClassValue.isInterface()) interfaces.add(baseClassValue.asClassDesc());
            else {
                if (baseClass != null) throw new RuntimeException("Class can't have more than one base class TODO");
                baseClass = baseClassValue.asClassDesc();
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
        ClassValue baseClassValue = EnvironmentUtils.getClassValue(this, baseClass.displayName()).orElse(null);

        if (baseClassValue != null) return baseClassValue.getEnvironment().getVariable(id);
        return Optional.empty();
    }

    @Override
    public List<VariableValue> getVariables() {
        return new ArrayList<>(variables);
    }



    @Override
    public void declareOperatorFunction(FunctionValue value) {
        List<ParameterExpression> parameters = value.getParameters();

        main:
        for (FunctionValue functionValue : operatorFunctions) {
            if (functionValue.getId().equals(value.getId())) {
                List<ParameterExpression> otherParameters = functionValue.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new EvaluationException(Text.translatable("meazy:runtime.function.operator.already_exists", value.getId()));
            }
        }

        operatorFunctions.add(value);
    }

    @Override
    public Set<FunctionValue> getOperatorFunctions() {
        return new HashSet<>(operatorFunctions);
    }



    @Override
    public Optional<FunctionValue> getFunction(String id, List<ClassDesc> args) {
        Optional<FunctionValue> functionValue = super.getFunction(id, args);
        if (functionValue.isPresent()) return functionValue;

        if (baseClass == null) return Optional.empty();
        ClassValue baseClassValue = EnvironmentUtils.getClassValue(this, baseClass).orElse(null);

        if (baseClassValue != null) return baseClassValue.getEnvironment().getFunction(id, args);
        return Optional.empty();
    }



    @Override
    public ConstructorValue declareConstructor(List<ParameterExpression> parameters, ConstructorEnvironment constructorEnvironment) {
        main:
        for (ConstructorValue constructorValue : constructors) {
            List<ParameterExpression> otherParameters = constructorValue.getParameters();
            if (parameters.size() != otherParameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
            }

            throw new EvaluationException(Text.translatable("meazy:runtime.constructor.already_exists"));
        }

        ConstructorValue value = new ConstructorValueImpl(parameters, List.of(), constructorEnvironment, Set.of());
        constructors.add(value);
        return value;
    }

    @Override
    public Set<ConstructorValue> getConstructors() {
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
}