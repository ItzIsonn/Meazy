package me.itzisonn_.meazy.parser.ast.expression.identifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.util.MiscUtils;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.UUID;

@NullMarked
public class VariableIdentifier extends Identifier {
    public VariableIdentifier(String id) {
        super(id);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        ResolvedVariable resolvedVariable = resolveVariable(environment, parent);

        if (resolvedVariable.getClassDesc() == null) {
            instructionsSet.getLocal(resolvedVariable.getType(), resolvedVariable.getSlot());
        }
        else if (resolvedVariable.getTarget() == null) {
            instructionsSet.getStaticField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
        }
        else {
            resolvedVariable.getTarget().emit(instructionsSet, environment, this);

            if (parent instanceof MemberExpression memberExpression && memberExpression.isNullSafe()) { //TODO test
                UUID nonnullLabel = instructionsSet.createAndInitLabel();
                UUID endLabel = instructionsSet.createAndInitLabel();

                instructionsSet.duplicate();
                instructionsSet.gotoLabelIfNonNull(nonnullLabel);

                instructionsSet.pop();
                instructionsSet.loadNull();
                instructionsSet.gotoLabel(endLabel);

                instructionsSet.bindLabel(nonnullLabel);
                instructionsSet.getField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
                instructionsSet.bindLabel(endLabel);
            }
            else {
                instructionsSet.getField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
            }
        }

        if (resolvedVariable.getType().isPrimitive()) {
            MiscUtils.boxPrimitive(instructionsSet, resolvedVariable.getType());
        }
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        ResolvedVariable resolvedVariable = resolveVariable(environment, parent);
        return DataType.of(MiscUtils.getBoxedType(resolvedVariable.getType()), resolvedVariable.isNullable());
    }

    private ResolvedVariable resolveVariable(Environment environment, Statement parent) {
        VariableValue variableValue = resolveMeazyVariable(environment, parent);
        if (variableValue == null) throw new RuntimeException("Can't find variable " + id);

        String className;
        if (variableValue.getParentEnvironment() instanceof ClassEnvironment classEnvironment) {
            if (classEnvironment.getModifiers().contains(Modifiers.OPEN())) {
                className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + classEnvironment.getId();
            }
            else {
                className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + EnvironmentUtils.getFileEnvironment(classEnvironment).orElseThrow().getClassName() + "$" + classEnvironment.getId();
            }
        }
        else if (variableValue.getParentEnvironment() instanceof FileEnvironment fileEnvironment) {
            className = fileEnvironment.getPackageName() + "." + fileEnvironment.getClassName();
        }
        else className = null;

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
    private VariableValue resolveMeazyVariable(Environment environment, Statement parent) {
        String id = getId();

        if (parent instanceof MemberExpression memberExpression) {
            DataType dataType = memberExpression.getObject().getType(environment, this);
            ClassDesc classDesc = dataType.getClassDesc();

            ClassValue classValue = EnvironmentUtils.getClassValue(environment, classDesc).orElse(null);
            if (classValue == null) return null;


            return classValue.getEnvironment().getVariable(id).orElse(null);
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