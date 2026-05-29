package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@NullMarked
public class FunctionDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final String id;
    @Nullable
    private final String classId;
    private final List<ParameterExpression> parameters;
    private final List<Statement> body;
    @Nullable
    private final DataType returnDataType;
    @Nullable
    private FunctionValue functionValue;

    public FunctionDeclarationStatement(Set<Modifier> modifiers, String id, @Nullable String classId, List<ParameterExpression> parameters, List<Statement> body, @Nullable DataType returnDataType) {
        super(modifiers);
        this.id = id;
        this.classId = classId;
        this.parameters = parameters;
        this.body = body;
        this.returnDataType = returnDataType;
    }

    public FunctionDeclarationStatement(Set<Modifier> modifiers, String id, List<ParameterExpression> parameters, List<Statement> body, @Nullable DataType returnDataType) {
        this(modifiers, id, null, parameters, body, returnDataType);
    }

    @Override
    public void declare(Environment environment) {
        if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
            throw new RuntimeException("CANT DECLARE FUNCTION HERE TODO");
        }

        boolean isShared = modifiers.contains(Modifiers.SHARED()) || environment instanceof FileEnvironment;

        FunctionEnvironment functionEnvironment = Registries.FUNCTION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                functionDeclarationEnvironment, null, null, returnDataType, isShared
        );

        functionValue = functionDeclarationEnvironment.declareFunction(
                id,
                parameters,
                returnDataType,
                functionEnvironment
        );
    }

    @Override
    public void resolve(Environment environment) {
        if (functionValue == null) {
            throw new RuntimeException("Function isn't declared TODO");
        }

        DataType returnDataType = functionValue.getReturnDataType();
        if (returnDataType != null) returnDataType.resolve(environment);

        DataType environmentReturnDataType = functionValue.getReturnDataType();
        if (environmentReturnDataType != null) environmentReturnDataType.resolve(environment);

        functionValue.getParameters().forEach(parameter -> parameter.getDataType().resolve(environment));
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        if (functionValue == null) {
            throw new RuntimeException("Declared function is unresolved TODO");
        }

        UUID startLabel = instructionsSet.createLabel();
        UUID endLabel = instructionsSet.createLabel();

        FunctionEnvironment functionEnvironment = functionValue.getEnvironment();
        functionEnvironment.setStartLabel(startLabel);
        functionEnvironment.setEndLabel(endLabel);

        boolean isShared = functionEnvironment.isShared();
        DataType returnDataType = functionValue.getReturnDataType();

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                returnDataType == null ? ConstantDescs.CD_void : returnDataType.getClassDesc(),
                functionValue.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList()
        );

        int accessFlags = 0;
        if (modifiers.contains(Modifiers.PRIVATE())) accessFlags |= AccessFlag.PRIVATE.mask();
        else if (modifiers.contains(Modifiers.PROTECTED())) accessFlags |= AccessFlag.PROTECTED.mask();
        else accessFlags |= AccessFlag.PUBLIC.mask();

        if (isShared) accessFlags |= AccessFlag.STATIC.mask();
        if (!modifiers.contains(Modifiers.OPEN())) accessFlags |= AccessFlag.FINAL.mask();

        instructionsSet.withMethod(
                functionValue.getId(),
                methodTypeDesc,
                accessFlags,
                bodyInstructions -> {
                    bodyInstructions.initLabel(startLabel);
                    bodyInstructions.initLabel(endLabel);

                    for (ParameterExpression parameter : functionValue.getParameters()) {
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
