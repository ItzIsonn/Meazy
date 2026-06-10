package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
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
public interface ClassEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment, ModifieredEnvironment {
    @Override
    ClassDeclarationEnvironment getParent();

    /**
     * @return This class environment's id
     */
    String getId();

    boolean isInterface();

    @Override
    String getFullClassName();

    ClassDesc getClassDesc();



    default Optional<FunctionEnvironment> getFunctionRecursively(String id, List<DataType> args) {
        Optional<FunctionEnvironment> functionEnvironment = getFunction(id, args);
        if (functionEnvironment.isPresent()) return functionEnvironment;

        if (getBaseClass() != null) {
            ClassDesc baseClass = EnvironmentUtils.resolveClassDesc(this, getBaseClass(), false);
            ClassEnvironment classEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass).orElseThrow();
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args);
            if (functionEnvironment.isPresent()) return functionEnvironment;
        }

        for (ClassDesc interfaceClassDesc : getInterfaces()) {
            ClassDesc baseClass = EnvironmentUtils.resolveClassDesc(this, interfaceClassDesc, false);
            ClassEnvironment classEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass).orElseThrow();
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args);
            if (functionEnvironment.isPresent()) return functionEnvironment;
        }

        return Optional.empty();
    }



    /**
     * Declares given operator function in this environment
     * @param functionEnvironment Function environment
     */
    void declareOperatorFunction(FunctionEnvironment functionEnvironment);

    /**
     * @param id Id
     * @param parameters Parameters
     * @return Declared operator function with given id and args or null
     */
    default Optional<FunctionEnvironment> getOperatorFunction(String id, List<ClassDesc> parameters) {
        main:
        for (FunctionEnvironment functionEnvironment : getOperatorFunctions()) {
            if (functionEnvironment.getId().equals(id)) {
                List<ParameterExpression> functionParameters = functionEnvironment.getParameters();
                if (parameters.size() != functionParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    ClassDesc functionParameterClassDesc = functionParameters.get(i).getDataType().getClassDesc();
                    ClassDesc parameterClassDesc = parameters.get(i);
                    if (!EnvironmentUtils.isInstanceOf(this, parameterClassDesc, functionParameterClassDesc)) continue main;
                }

                return Optional.of(functionEnvironment);
            }
        }

        return Optional.empty();
    }

    /**
     * @return All declared operator functions
     */
    Set<FunctionEnvironment> getOperatorFunctions();



    /**
     * @return ClassDesc of this class environment's base class
     */
    @Nullable
    ClassDesc getBaseClass();

    Set<ClassDesc> getInterfaces();

    void resolveBaseClasses();
}