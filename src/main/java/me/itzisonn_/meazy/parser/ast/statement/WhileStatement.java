package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironmentKt;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class WhileStatement implements LocalStatement {
    private final Expression condition;
    private final List<LocalStatement> body;

    public WhileStatement(Expression condition, List<LocalStatement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        if (!condition.getType(environment, this).equals(DataType.ofNonNull(ConstantDescs.CD_boolean))) {
            throw new RuntimeException("While statement must always use boolean TODO");
        }

        var conditionLabel = instructionsSet.createAndInitLabel();
        var endLabel = instructionsSet.createAndInitLabel();
        LoopEnvironment loopEnvironment = LoopEnvironmentKt.LoopEnvironment(environment, conditionLabel, endLabel);

        instructionsSet.bindLabel(conditionLabel);
        condition.emit(instructionsSet, environment, this);
        instructionsSet.gotoLabelIfEqualsZero(endLabel);

        for (Statement statement : body) {
            statement.emit(instructionsSet, loopEnvironment, this);
        }

        instructionsSet.gotoLabel(conditionLabel);
        instructionsSet.bindLabel(endLabel);
    }

    @Override
    public boolean alwaysReturns() {
        for (LocalStatement localStatement : body) {
            if (localStatement.alwaysReturns()) return true;
        }

        return false;
    }
}
