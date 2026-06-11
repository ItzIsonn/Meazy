package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironmentKt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ConstantDescs;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class IfStatement implements LocalStatement {
    @Nullable
    private final Expression condition;
    private final List<LocalStatement> body;
    @Nullable
    private final IfStatement elseStatement;

    public IfStatement(@Nullable Expression condition, List<LocalStatement> body, @Nullable IfStatement elseStatement) {
        this.condition = condition;
        this.body = body;
        this.elseStatement = elseStatement;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        var startLabel = instructionsSet.createAndInitLabel();
        var elseLabel = instructionsSet.createAndInitLabel();
        LocalVariableDeclarationEnvironment ifEnvironment = LocalVariableDeclarationEnvironmentKt.LocalVariableDeclarationEnvironment(
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

        var endLabel = instructionsSet.createAndInitLabel();
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

    @Override
    public boolean alwaysReturns() {
        if (elseStatement == null) return false;

        for (LocalStatement localStatement : body) {
            if (!localStatement.alwaysReturns()) return false;
        }

        return elseStatement.alwaysReturns();
    }
}
