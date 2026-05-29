package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@NullMarked
public class ConstructorDeclarationStatement extends ModifierStatement implements Statement {
    private final List<ParameterExpression> parameters;
    private final List<Statement> body;

    public ConstructorDeclarationStatement(Set<Modifier> modifiers, List<ParameterExpression> parameters, List<Statement> body) {
        super(modifiers);
        this.parameters = parameters;
        this.body = body;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
            throw new RuntimeException("CANT DECLARE FUNCTION HERE TODO");
        }

        UUID startLabel = instructionsSet.createLabel();
        UUID endLabel = instructionsSet.createLabel();

        ConstructorEnvironment constructorEnvironment = Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(constructorDeclarationEnvironment, startLabel, endLabel);

        constructorDeclarationEnvironment.declareConstructor(
                parameters.stream().map(parameter -> new ParameterExpression(parameter.getId(), parameter.getDataType(), parameter.isConstant())).toList(),
                constructorEnvironment
        );

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                parameters.stream().map(p -> p.getType(environment, this).getClassDesc()).toList()
        );

        int accessFlags = 0;
        if (modifiers.contains(Modifiers.PRIVATE())) accessFlags |= AccessFlag.PRIVATE.mask();
        else if (modifiers.contains(Modifiers.PROTECTED())) accessFlags |= AccessFlag.PROTECTED.mask();
        else accessFlags |= AccessFlag.PUBLIC.mask();

        instructionsSet.withConstructor(
                methodTypeDesc,
                accessFlags,
                bodyInstructions -> {
                    bodyInstructions.initLabel(startLabel);
                    bodyInstructions.initLabel(endLabel);

                    for (ParameterExpression parameter : parameters) {
                        VariableValue parameterValue = constructorEnvironment.declareVariable(parameter.getId(), parameter.getDataType(), parameter.isConstant(), null);
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
