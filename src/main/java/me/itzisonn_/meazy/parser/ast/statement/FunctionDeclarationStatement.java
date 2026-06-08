package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.variable_value.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.*;

@Getter
@NullMarked
public class FunctionDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final String id;
    @Nullable
    private final String classId;
    private final List<ParameterExpression> parameters;
    private final List<LocalStatement> body;
    @Nullable
    private final DataType returnDataType;
    @Nullable
    private final Expression returnDataTypeValue;
    @Nullable
    private FunctionEnvironment functionEnvironment;

    public FunctionDeclarationStatement(
            Set<Modifier> modifiers, String id, @Nullable String classId, List<ParameterExpression> parameters,
            List<LocalStatement> body, @Nullable DataType returnDataType, @Nullable Expression returnDataTypeValue
    ) {
        super(modifiers);
        this.id = id;
        this.classId = classId;
        this.parameters = parameters;
        this.body = body;
        this.returnDataType = returnDataType;
        this.returnDataTypeValue = returnDataTypeValue;
    }

    public FunctionDeclarationStatement(Set<Modifier> modifiers, String id, List<ParameterExpression> parameters, List<LocalStatement> body, @Nullable DataType returnDataType) {
        this(modifiers, id, null, parameters, body, returnDataType, null);
    }

    @Override
    public void declare(Environment environment) { //TODO check whether abstract function isn't overridden
        if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
            throw new RuntimeException("CANT DECLARE FUNCTION HERE TODO");
        }

        boolean isShared = modifiers.contains(Modifiers.SHARED()) || environment instanceof FileEnvironment;

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionDeclarationEnvironment, null, null, id, parameters,
                returnDataType, isShared, modifiers
        );

        functionDeclarationEnvironment.declareFunction(functionEnvironment);
        this.functionEnvironment = functionEnvironment;

        if (modifiers.contains(Modifiers.ABSTRACT())) return;

        for (LocalStatement localStatement : body) {
            if (localStatement.alwaysReturns()) return;
        }

        if (returnDataType == null) {
            body.add(new ReturnStatement(null));
            return;
        }

        throw new RuntimeException("Function with id " + id + " doesn't always return a value");
    }

    @Override
    public void resolve(Environment environment) {
        if (functionEnvironment == null) {
            throw new RuntimeException("Function isn't declared TODO");
        }

        DataType returnDataType;
        if (functionEnvironment.getReturnDataType() != null) returnDataType = functionEnvironment.getReturnDataType();
        else if (returnDataTypeValue != null) {
            returnDataType = returnDataTypeValue.getType(environment, this);
            functionEnvironment.setReturnDataType(returnDataType);
        }
        else returnDataType = null;

        if (returnDataType != null) returnDataType.resolve(environment);

        functionEnvironment.getParameters().forEach(parameter -> parameter.getDataType().resolve(environment));
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        if (functionEnvironment == null) {
            throw new RuntimeException("Declared function is unresolved TODO");
        }

        UUID startLabel = instructionsSet.createLabel();
        UUID endLabel = instructionsSet.createLabel();
        functionEnvironment.setStartLabel(startLabel);
        functionEnvironment.setEndLabel(endLabel);

        if (functionEnvironment.getParent() instanceof ClassEnvironment classEnvironment) {
            if (classEnvironment.getBaseClass() != null) {
                EnvironmentUtils.getClassEnvironment(classEnvironment, classEnvironment.getBaseClass()).orElseThrow()
                        .getFunctionRecursively(this.functionEnvironment.getId(), this.functionEnvironment.getParameters().stream().map(ParameterExpression::getDataType).toList())
                        .ifPresent(f -> {
                            if (!f.getModifiers().contains(Modifiers.OPEN()) && !f.getModifiers().contains(Modifiers.ABSTRACT())) {
                                throw new RuntimeException("Can't override non-open function " + id);
                            }
                            if (!this.functionEnvironment.getModifiers().contains(Modifiers.OVERRIDE())) throw new RuntimeException("Must specify override keyword on function " + id);
                        });
            }

            for (ClassDesc interfaceClassDesc : classEnvironment.getInterfaces()) {
                EnvironmentUtils.getClassEnvironment(classEnvironment, interfaceClassDesc).orElseThrow()
                        .getFunctionRecursively(this.functionEnvironment.getId(), this.functionEnvironment.getParameters().stream().map(ParameterExpression::getDataType).toList())
                        .ifPresent(f -> {
                            if (!f.getModifiers().contains(Modifiers.OPEN()) && !f.getModifiers().contains(Modifiers.ABSTRACT())) {
                                throw new RuntimeException("Can't override non-open function " + id);
                            }
                            if (!this.functionEnvironment.getModifiers().contains(Modifiers.OVERRIDE())) throw new RuntimeException("Must specify override keyword on function " + id);
                        });
            }
        }

        boolean isShared = functionEnvironment.isShared();
        DataType returnDataType = this.functionEnvironment.getReturnDataType();

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                returnDataType == null ? ConstantDescs.CD_void : returnDataType.getClassDesc(),
                this.functionEnvironment.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList()
        );

        Set<AccessFlag> accessFlags = new HashSet<>();
        if (this.functionEnvironment.getModifiers().contains(Modifiers.PRIVATE())) accessFlags.add(AccessFlag.PRIVATE);
        else if (this.functionEnvironment.getModifiers().contains(Modifiers.PROTECTED())) accessFlags.add(AccessFlag.PROTECTED);
        else accessFlags.add(AccessFlag.PUBLIC);

        if (isShared) accessFlags.add(AccessFlag.STATIC);
        if (this.functionEnvironment.getModifiers().contains(Modifiers.ABSTRACT())) accessFlags.add(AccessFlag.ABSTRACT);
        else if (!this.functionEnvironment.getModifiers().contains(Modifiers.OPEN()) && !(functionEnvironment.getParent() instanceof ClassEnvironment classEnvironment && classEnvironment.isInterface())) {
            accessFlags.add(AccessFlag.FINAL);
        }

        instructionsSet.withMethod(
                this.functionEnvironment.getId(),
                methodTypeDesc,
                accessFlags,
                bodyInstructions -> {
                    bodyInstructions.initLabel(startLabel);
                    bodyInstructions.initLabel(endLabel);

                    for (ParameterExpression parameter : this.functionEnvironment.getParameters()) {
                        VariableValue parameterValue = functionEnvironment.declareVariable(
                                parameter.getId(),
                                parameter.getDataType(),
                                parameter.isConstant(),
                                null
                        );

                        bodyInstructions.setLocalName(parameterValue.getSlot(), parameter.getId(), parameter.getDataType().getClassDesc(), startLabel, endLabel);
                    }

                    bodyInstructions.bindLabel(startLabel);
                    for (Statement statement : body) {
                        statement.emit(bodyInstructions, functionEnvironment, this);
                    }
                    bodyInstructions.bindLabel(endLabel);
                }
        );
    }
}
