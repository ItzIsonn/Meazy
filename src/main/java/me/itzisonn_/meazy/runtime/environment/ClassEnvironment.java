package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents environment for classes
 */
@NullMarked
public interface ClassEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    @Override
    ClassDeclarationEnvironment getParent();

    /**
     * @return This class environment's id
     */
    String getId();

    /**
     * @return This class environment's modifiers
     */
    Set<Modifier> getModifiers();

    boolean isInterface();

    @Override
    String getFullClassName();



    default Optional<FunctionValue> getFunctionRecursively(String id, List<ClassDesc> parameters) {
        Optional<FunctionValue> functionValue = getFunction(id, parameters);
        if (functionValue.isPresent()) return functionValue;

        if (getBaseClass() != null) {
            ClassDesc baseClass = EnvironmentUtils.resolveClassDesc(this, getBaseClass(), false);
            ClassValue classValue = EnvironmentUtils.getClassValue(this, baseClass).orElseThrow();
            functionValue = classValue.getEnvironment().getFunctionRecursively(id, parameters);
            if (functionValue.isPresent()) return functionValue;
        }

        for (ClassDesc interfaceClassDesc : getInterfaces()) {
            ClassDesc baseClass = EnvironmentUtils.resolveClassDesc(this, interfaceClassDesc, false);
            ClassValue classValue = EnvironmentUtils.getClassValue(this, baseClass).orElseThrow();
            functionValue = classValue.getEnvironment().getFunctionRecursively(id, parameters);
            if (functionValue.isPresent()) return functionValue;
        }

        return Optional.empty();
    }



    /**
     * Declares given operator function in this environment
     * @param value FunctionValue
     */
    void declareOperatorFunction(FunctionValue value);

    /**
     * @param id Id
     * @param parameters Parameters
     * @return Declared operator function with given id and args or null
     */
    default Optional<FunctionValue> getOperatorFunction(String id, List<ClassDesc> parameters) {
        main:
        for (FunctionValue functionValue : getOperatorFunctions()) {
            if (functionValue.getId().equals(id)) {
                List<ParameterExpression> functionParameters = functionValue.getParameters();
                if (parameters.size() != functionParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    ClassDesc functionParameterClassDesc = functionParameters.get(i).getDataType().getClassDesc();
                    ClassDesc parameterClassDesc = parameters.get(i);
                    if (!EnvironmentUtils.isInstanceOf(this, parameterClassDesc, functionParameterClassDesc)) continue main;
                }

                return Optional.of(functionValue);
            }
        }

        return Optional.empty();
    }

    /**
     * @return All declared operator functions
     */
    Set<FunctionValue> getOperatorFunctions();



    /**
     * @return ClassDesc of this class environment's base class
     */
    @Nullable
    ClassDesc getBaseClass();

    Set<ClassDesc> getInterfaces();

    void resolveBaseClasses();
}