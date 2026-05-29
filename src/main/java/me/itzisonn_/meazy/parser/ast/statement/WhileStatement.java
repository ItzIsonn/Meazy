package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class WhileStatement implements LocalBodyStatement {
    private final Expression condition;
    private final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        if (!condition.getType(environment, this).equals(DataType.ofNonNull(ConstantDescs.CD_boolean))) {
            throw new RuntimeException("While statement must always use boolean TODO");
        }

        UUID conditionLabel = instructionsSet.createAndInitLabel();
        UUID endLabel = instructionsSet.createAndInitLabel();
        LoopEnvironment loopEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment, conditionLabel, endLabel);

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
        for (Statement statement : body) {
            if (statement instanceof LocalBodyStatement localBodyStatement) {
                if (localBodyStatement.alwaysReturns()) return true;
            }
        }

        return false;
    }
}
