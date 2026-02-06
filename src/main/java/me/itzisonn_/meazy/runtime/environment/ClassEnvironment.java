package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Represents environment for classes
 */
@NullMarked
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
     * @return Declared operator function with given id and args or null
     */
    @Nullable
    default FunctionValue getOperatorFunction(String id, List<RuntimeValue> args) {
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



    /** TODO javadoc
     * @return All base classes of this class environment
     */
    @Nullable
    String getBaseClass();

//    /**
//     * @return All base classes of this class environment and their base classes
//     */
//    default List<String> getAllBaseClasses() {
//        Set<ClassEnvironment> baseClasses = new HashSet<>();
//
//        for (ClassEnvironment baseClass : getBaseClasses()) {
//            baseClasses.add(baseClass);
//            baseClasses.addAll(baseClass.getDeepBaseClasses());
//        }
//
//        return baseClasses;
//    }
}