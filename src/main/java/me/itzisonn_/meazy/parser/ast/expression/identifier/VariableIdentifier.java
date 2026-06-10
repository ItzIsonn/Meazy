package me.itzisonn_.meazy.parser.ast.expression.identifier;

import kotlin.uuid.Uuid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.VariableValue;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;

@NullMarked
public class VariableIdentifier extends Identifier {
    public VariableIdentifier(String id) {
        super(id);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        ResolvedVariable resolvedVariable = resolveVariable(environment, parent);

        if (resolvedVariable.getClassDesc() == null) {
            instructionsSet.getLocal(resolvedVariable.getType(), resolvedVariable.getSlot());
        }
        else if (resolvedVariable.getTarget() == null) {
            instructionsSet.getStaticField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
        }
        else {
            resolvedVariable.getTarget().emit(instructionsSet, environment, this);
            Uuid endLabel = null;

            if (parent instanceof MemberExpression memberExpression) {
                if (!memberExpression.isNullSafe()) {
                    if (resolvedVariable.getTarget().getType(environment, this).isNullable()) {
                        throw new RuntimeException("Unsafe member call of function " + id + " on object of type " + resolvedVariable.getClassDesc().descriptorString());
                    }
                }
                else {
                    var nonnullLabel = instructionsSet.createAndInitLabel();
                    endLabel = instructionsSet.createAndInitLabel();

                    instructionsSet.duplicate();
                    instructionsSet.gotoLabelIfNonNull(nonnullLabel);

                    instructionsSet.pop();
                    instructionsSet.loadNull();
                    instructionsSet.gotoLabel(endLabel);

                    instructionsSet.bindLabel(nonnullLabel);
                }
            }

            instructionsSet.getField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());

            if (endLabel != null) {
                instructionsSet.bindLabel(endLabel);
            }
        }
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        ResolvedVariable resolvedVariable = resolveVariable(environment, parent);
        return DataType.of(resolvedVariable.getType(), resolvedVariable.isNullable());
    }

    private ResolvedVariable resolveVariable(Environment environment, ProgramUnit parent) {
        VariableValue variableValue = resolveMeazyVariable(environment, parent);
        if (variableValue == null) throw new RuntimeException("Can't find variable " + id);

        String className = variableValue.getParentEnvironment().getFullClassName();

        Expression target;
        if (parent instanceof MemberExpression memberExpression) {
            target = memberExpression.getObject() instanceof ClassIdentifier ? null : memberExpression.getObject();
        }
        else if (variableValue.getModifiers().contains(Modifiers.SHARED()) || variableValue.getParentEnvironment() instanceof FileEnvironment) {
            target = null;
        }
        else {
            target = new ThisLiteral();
        }

        return new ResolvedVariable(
                className == null ? null : ClassDesc.of(className),
                className == null ? variableValue.getSlot() : -1,
                variableValue.getId(),
                variableValue.getDataType().getClassDesc(),
                variableValue.getDataType().isNullable(),
                target
        );
    }

    @Nullable
    private VariableValue resolveMeazyVariable(Environment environment, ProgramUnit parent) {
        String id = getId();

        if (parent instanceof MemberExpression memberExpression) {
            DataType dataType = memberExpression.getObject().getType(environment, this);
            ClassDesc classDesc = dataType.getClassDesc();

            ClassEnvironment classEnvironment = EnvironmentUtils.getClassEnvironment(environment, classDesc).orElse(null);
            if (classEnvironment == null) return null;

            return classEnvironment.getVariable(id).orElse(null);
        }

        return EnvironmentUtils.getVariableValue(environment, id).orElse(null);
    }

    @Getter
    @AllArgsConstructor
    private static class ResolvedVariable {
        @Nullable
        private final ClassDesc classDesc;
        private final int slot;
        private final String id;
        private final ClassDesc type;
        private final boolean isNullable;
        @Nullable
        private final Expression target;
    }
}