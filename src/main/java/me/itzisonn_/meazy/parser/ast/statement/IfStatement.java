package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironmentKt;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ConstantDescs;
import java.util.List;

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
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        var startLabel = instructions.createAndInitLabel();
        var elseLabel = instructions.createAndInitLabel();
        LocalVariableDeclarationEnvironment ifEnvironment = LocalVariableDeclarationEnvironmentKt.LocalVariableDeclarationEnvironment(
                environment, startLabel, elseLabel
        );

        if (condition == null) {
            instructions.bindLabel(startLabel);

            for (Statement statement : body) {
                statement.emit(instructions, ifEnvironment, this);
            }

            instructions.bindLabel(elseLabel);
            return;
        }

        DataType conditionType = condition.getType(environment, this);
        if (conditionType.isNullable() || (!conditionType.getClassDesc().equals(ConstantDescs.CD_boolean) &&
                !conditionType.getClassDesc().equals(ConstantDescs.CD_Boolean))) {
            throw new RuntimeException("If statement must always use boolean TODO but uses " + condition.getType(environment, this));
        }

        var endLabel = instructions.createAndInitLabel();
        condition.emit(instructions, environment, this);
        instructions.convertToBooleanType(conditionType.getClassDesc().equals(ConstantDescs.CD_Boolean), false);
        instructions.gotoLabelIfEqualsZero(elseLabel);

        instructions.bindLabel(startLabel);
        for (Statement statement : body) {
            statement.emit(instructions, ifEnvironment, this);
        }
        instructions.gotoLabel(endLabel);

        instructions.bindLabel(elseLabel);
        if (elseStatement != null) {
            elseStatement.emit(instructions, environment, parent);
        }

        instructions.bindLabel(endLabel);
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
