package me.itzisonn_.meazy.parser.ast.statement;

import kotlin.Unit;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.VariableValue;
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NullMarked
public class ConstructorDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final List<ParameterExpression> parameters;
    private final List<LocalStatement> body;
    @Nullable
    private ConstructorEnvironment constructorEnvironment;

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

        ConstructorEnvironment constructorEnvironment = ConstructorEnvironmentKt.ConstructorEnvironment(
                constructorDeclarationEnvironment, null, null, modifiers, parameters
        );

        constructorDeclarationEnvironment.declareConstructor(constructorEnvironment);
        this.constructorEnvironment = constructorEnvironment;

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
        if (constructorEnvironment == null) {
            throw new RuntimeException("Constructor isn't declared TODO");
        }

        constructorEnvironment.getParameters().forEach(parameter -> parameter.getDataType().resolve(environment));
    }

    @Override
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        if (constructorEnvironment == null) {
            throw new RuntimeException("Declared function is unresolved TODO");
        }

        var startLabel = instructions.createLabel();
        var endLabel = instructions.createLabel();
        constructorEnvironment.setStartLabel(startLabel);
        constructorEnvironment.setEndLabel(endLabel);

        MethodTypeDesc methodTypeDesc = MethodTypeDesc.of(
                ConstantDescs.CD_void,
                this.constructorEnvironment.getParameters().stream().map(p -> p.getType(environment, this).getClassDesc()).toList()
        );

        Set<AccessFlag> accessFlags = new HashSet<>();
        if (this.constructorEnvironment.getModifiers().contains(Modifiers.PRIVATE())) accessFlags.add(AccessFlag.PRIVATE);
        else if (this.constructorEnvironment.getModifiers().contains(Modifiers.PROTECTED())) accessFlags.add(AccessFlag.PROTECTED);
        else accessFlags.add(AccessFlag.PUBLIC);

        instructions.withConstructor(
                methodTypeDesc,
                accessFlags,
                bodyInstructions -> {
                    bodyInstructions.initLabel(startLabel);
                    bodyInstructions.initLabel(endLabel);

                    for (ParameterExpression parameter : this.constructorEnvironment.getParameters()) {
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
                    return Unit.INSTANCE;
                }
        );
    }
}
