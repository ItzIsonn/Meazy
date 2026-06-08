package me.itzisonn_.meazy.parser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.variable_value.VariableValue;
import me.itzisonn_.meazy.util.MiscUtils;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;

@Getter
@NullMarked
public class AssignmentStatement implements LocalStatement {
    private final Expression id;
    private final Expression value;

    public AssignmentStatement(Expression id, Expression value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        ResolvedVariable resolvedVariable = resolveVariable(environment, parent);
        if (resolvedVariable.isConstant()) throw new RuntimeException("Can't reassign constant variable " + resolvedVariable.getId() + " TODO");

        ClassDesc variableType = resolvedVariable.getType();
        ClassDesc valueType = value.getType(environment, this).getClassDesc();

        if (resolvedVariable.getClassDesc() != null && resolvedVariable.getTarget() != null) {
            resolvedVariable.getTarget().emit(instructionsSet, environment, this);
        }

        value.emit(instructionsSet, environment, this);

        if (!EnvironmentUtils.isInstanceOf(environment, valueType, variableType)) {
            if (!MiscUtils.convertPrimitiveOrBoxed(instructionsSet, valueType, variableType)) {
                throw new RuntimeException("Can't assign value of type " + valueType + " to variable with type " + variableType);
            }
        }

        if (resolvedVariable.getClassDesc() == null) {
            instructionsSet.storeLocal(resolvedVariable.getType(), resolvedVariable.getSlot());
        }
        else if (resolvedVariable.getTarget() == null) {
            instructionsSet.storeStaticField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
        }
        else {
            instructionsSet.storeField(resolvedVariable.getClassDesc(), resolvedVariable.getId(), resolvedVariable.getType());
        }
    }

    @Override
    public boolean alwaysReturns() {
        return false;
    }



    private ResolvedVariable resolveVariable(Environment environment, ProgramUnit parent) {
        VariableValue variableValue = resolveMeazyVariable(environment);
        if (variableValue == null) throw new RuntimeException("Can't find variable");

        String className;
        if (variableValue.getParentEnvironment() instanceof ClassEnvironment classEnvironment) {
            className = classEnvironment.getId();
        }
        else if (variableValue.getParentEnvironment() instanceof FileEnvironment fileEnvironment) {
            className = fileEnvironment.getPackageName(); //TODO
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
                variableValue.isConstant(),
                variableValue.getId(),
                variableValue.getDataType().getClassDesc(),
                target
        );
    }

    @Nullable
    private VariableValue resolveMeazyVariable(Environment environment) {
        if (id instanceof MemberExpression memberExpression) {
            if (!(memberExpression.getMember() instanceof VariableIdentifier variableIdentifier)) {
                throw new RuntimeException("Cant assign value to not variable TODO");
            }

            ClassDesc classDesc = memberExpression.getObject().getType(environment, this).getClassDesc();
            ClassEnvironment classEnvironment = EnvironmentUtils.getClassEnvironment(environment, classDesc).orElse(null);
            if (classEnvironment == null) return null;

            return classEnvironment.getVariable(variableIdentifier.getId()).orElse(null);
        }

        if (!(id instanceof VariableIdentifier variableIdentifier)) {
            throw new RuntimeException("Cant assign value to not variable TODO");
        }

        return EnvironmentUtils.getVariableValue(environment, variableIdentifier.getId()).orElse(null);
    }

    @Getter
    @AllArgsConstructor
    private static class ResolvedVariable {
        @Nullable
        private final ClassDesc classDesc;
        private final int slot;
        private final boolean isConstant;
        private final String id;
        private final ClassDesc type;
        @Nullable
        private final Expression target;
    }
}
