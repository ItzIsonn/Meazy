package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ConstantDescs;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class IfStatement implements Statement {
    @Nullable
    private final Expression condition;
    private final List<Statement> body;
    @Nullable
    private final IfStatement elseStatement;

    public IfStatement(@Nullable Expression condition, List<Statement> body, @Nullable IfStatement elseStatement) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        UUID startLabel = instructionsSet.createAndInitLabel();
        UUID elseLabel = instructionsSet.createAndInitLabel();
        LocalVariableDeclarationEnvironment ifEnvironment = Registries.LOCAL_VARIABLE_DECLARATION_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                environment, startLabel, elseLabel
        );

        if (condition == null) {
            instructionsSet.bindLabel(startLabel);

            for (Statement statement : body) {
                statement.emit(instructionsSet, ifEnvironment, this);
            }

            instructionsSet.bindLabel(elseLabel);
            return;
        }

        DataType conditionType = condition.getType(environment, this);
        if (conditionType.isNullable() || (!conditionType.getClassDesc().equals(ConstantDescs.CD_boolean) &&
                !conditionType.getClassDesc().equals(ConstantDescs.CD_Boolean))) {
            throw new RuntimeException("If statement must always use boolean TODO but uses " + condition.getType(environment, this));
        }


        UUID endLabel = instructionsSet.createAndInitLabel();
        condition.emit(instructionsSet, environment, this);
        instructionsSet.convertToBooleanType(conditionType.getClassDesc().equals(ConstantDescs.CD_Boolean), false);
        instructionsSet.gotoLabelIfEqualsZero(elseLabel);

        instructionsSet.bindLabel(startLabel);
        for (Statement statement : body) {
            statement.emit(instructionsSet, ifEnvironment, this);
        }
        instructionsSet.gotoLabel(endLabel);

        instructionsSet.bindLabel(elseLabel);
        if (elseStatement != null) {
            elseStatement.emit(instructionsSet, environment, parent);
        }

        instructionsSet.bindLabel(endLabel);
    }
}
