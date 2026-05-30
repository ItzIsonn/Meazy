package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
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
public class ConstructorDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final List<ParameterExpression> parameters;
    private final List<LocalStatement> body;
    @Nullable
    private ConstructorValue constructorValue;

    public ConstructorDeclarationStatement(Set<Modifier> modifiers, List<ParameterExpression> parameters, List<LocalStatement> body) {
        super(modifiers);
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void declare(Environment environment) {
        if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
            throw new RuntimeException("CANT DECLARE CONSTRUCTOR HERE TODO");
        }

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                constructorDeclarationEnvironment, null, null, modifiers
        );

        constructorValue = constructorDeclarationEnvironment.declareConstructor(
                parameters,
                constructorEnvironment
        );

        boolean alwaysReturns = false;
        boolean hasBaseCall = false;

        for (LocalStatement localStatement : body) {
            if (localStatement.alwaysReturns()) alwaysReturns = true;
            if (localStatement instanceof BaseCallStatement) hasBaseCall = true;
        }

        if (!hasBaseCall) body.addFirst(new BaseCallStatement(List.of()));
        if (!alwaysReturns) body.add(new ReturnStatement(null));
    }

    @Override
    public void resolve(Environment environment) {
        if (constructorValue == null) {
            throw new RuntimeException("Constructor isn't declared TODO");
        }

        constructorValue.getParameters().forEach(parameter -> parameter.getDataType().resolve(environment));
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        if (constructorValue == null) {
            throw new RuntimeException("Declared function is unresolved TODO");
        }

        UUID startLabel = instructionsSet.createLabel();
        UUID endLabel = instructionsSet.createLabel();

        ConstructorEnvironment constructorEnvironment = constructorValue.getEnvironment();

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                constructorValue.getParameters().stream().map(p -> p.getType(environment, this).getClassDesc()).toList()
        );

        int accessFlags = 0;
        if (constructorValue.getModifiers().contains(Modifiers.PRIVATE())) accessFlags |= AccessFlag.PRIVATE.mask();
        else if (constructorValue.getModifiers().contains(Modifiers.PROTECTED())) accessFlags |= AccessFlag.PROTECTED.mask();
        else accessFlags |= AccessFlag.PUBLIC.mask();

        instructionsSet.withConstructor(
                methodTypeDesc,
                accessFlags,
                bodyInstructions -> {
                    bodyInstructions.initLabel(startLabel);
                    bodyInstructions.initLabel(endLabel);

                    for (ParameterExpression parameter : constructorValue.getParameters()) {
                        VariableValue parameterValue = constructorEnvironment.declareVariable(
                                parameter.getId(),
                                parameter.getDataType(),
                                parameter.isConstant(),
                                null
                        );

                        bodyInstructions.setLocalName(parameterValue.getSlot(), parameter.getId(), parameter.getDataType().getClassDesc(), startLabel, endLabel);
                    }

                    bodyInstructions.bindLabel(startLabel);
                    for (Statement statement : body) {
                        statement.emit(bodyInstructions, constructorEnvironment, this);
                    }
                    bodyInstructions.bindLabel(endLabel);
                }
        );
    }
}
