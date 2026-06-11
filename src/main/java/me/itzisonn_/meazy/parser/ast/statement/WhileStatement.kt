package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironmentKt;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;
import java.util.List;

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
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        if (!condition.getType(environment, this).equals(DataType.ofNonNull(ConstantDescs.CD_boolean))) {
            throw new RuntimeException("While statement must always use boolean TODO");
        }

        var conditionLabel = instructions.createAndInitLabel();
        var endLabel = instructions.createAndInitLabel();
        LoopEnvironment loopEnvironment = LoopEnvironmentKt.LoopEnvironment(environment, conditionLabel, endLabel);

        instructions.bindLabel(conditionLabel);
        condition.emit(instructions, environment, this);
        instructions.gotoLabelIfEqualsZero(endLabel);

        for (Statement statement : body) {
            statement.emit(instructions, loopEnvironment, this);
        }

        instructions.gotoLabel(conditionLabel);
        instructions.bindLabel(endLabel);
    }

    @Override
    public boolean alwaysReturns() {
        for (LocalStatement localStatement : body) {
            if (localStatement.alwaysReturns()) return true;
        }

        return false;
    }
}
