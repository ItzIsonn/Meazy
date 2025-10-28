package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents environment for classes
 */
public interface ClassEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    /**
     * @return This class environment's id
     */
    String getId();

    /**
     * @return This class environment's modifiers
     */
    Set<Modifier> getModifiers();



    /**
     * Declares given operator function in this environment
     * @param value FunctionValue
     */
    void declareOperatorFunction(FunctionValue value);

    /**
     * @param id Id
     * @param args Args
     *
     * @return Declared operator function with given id and args or null
     * @throws NullPointerException If either id or args is null
     */
    default FunctionValue getOperatorFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        main:
        for (FunctionValue functionValue : getOperatorFunctions()) {
            if (functionValue.getId().equals(id)) {
                List<ParameterExpression> parameters = functionValue.getParameters();
                if (args.size() != parameters.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    if (!parameters.get(i).getDataType().isMatches(args.get(i), getFileEnvironment())) continue main;
                }

                return functionValue;
            }
        }

        return null;
    }

    /**
     * @return All declared operator functions
     */
    Set<FunctionValue> getOperatorFunctions();



    /**
     * Adds given classEnvironment as base class
     * @param classEnvironment ClassEnvironment to add
     */
    void addBaseClass(ClassEnvironment classEnvironment);

    /**
     * @param id Class's id
     * @return Base class of this class environment with given id or null
     * @throws NullPointerException If given id is null
     */
    default ClassEnvironment getBaseClass(String id) {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassEnvironment classEnvironment : getBaseClasses()) {
            if (classEnvironment.getId().equals(id)) return classEnvironment;
        }

        return null;
    }

    /**
     * @param id Class's id
     * @return Base class of this class environment and it's base classes with given id or null
     * @throws NullPointerException If given id is null
     */
    default ClassEnvironment getDeepBaseClass(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassEnvironment classEnvironment : getDeepBaseClasses()) {
            if (classEnvironment.getId().equals(id)) return classEnvironment;
        }

        return null;
    }

    /**
     * @return All base classes of this class environment
     */
    Set<ClassEnvironment> getBaseClasses();

    /**
     * @return All base classes of this class environment and their base classes
     */
    default Set<ClassEnvironment> getDeepBaseClasses() {
        Set<ClassEnvironment> baseClasses = new HashSet<>();

        for (ClassEnvironment baseClass : getBaseClasses()) {
            baseClasses.add(baseClass);
            baseClasses.addAll(baseClass.getDeepBaseClasses());
        }

        return baseClasses;
    }
}