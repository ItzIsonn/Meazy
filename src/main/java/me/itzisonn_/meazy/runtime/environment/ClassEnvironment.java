package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents environment for classes
 */
public interface ClassEnvironment extends Environment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    /**
     * @return This ClassEnvironment's id
     */
    String getId();

    /**
     * @return This ClassEnvironment's modifiers
     */
    Set<Modifier> getModifiers();



    /**
     * Declares given operator function in this environment
     * @param value FunctionValue
     */
    void declareOperatorFunction(FunctionValue value);

    /**
     * @param id Function's id
     * @param args Function's args
     * @return Declared operator function with given id and args or null
     * @throws NullPointerException If either id or args is null
     */
    default FunctionValue getOperatorFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        main:
        for (FunctionValue functionValue : getOperatorFunctions()) {
            if (functionValue.getId().equals(id)) {
                List<CallArgExpression> callArgExpressions = functionValue.getArgs();

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    if (!callArgExpressions.get(i).getDataType().isMatches(args.get(i))) continue main;
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
     * @return Base class of this ClassEnvironment with given id or null
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
     * @return Base class of this ClassEnvironment and it's base classes with given id or null
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
     * @return All base classes of this ClassEnvironment
     */
    Set<ClassEnvironment> getBaseClasses();

    /**
     * @return All base classes of this ClassEnvironment and their base classes
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