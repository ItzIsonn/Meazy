package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.reflect.AccessFlag;
import java.util.Set;

@Getter
@NullMarked
public class VariableDeclarationStatement extends ModifierStatement implements Statement {
    private final boolean isConstant;
    private final String id;
    private final DataType dataType;
    @Nullable
    private final Expression value;

    public VariableDeclarationStatement(Set<Modifier> modifiers, boolean isConstant, String id, DataType dataType, @Nullable Expression value) {
        super(modifiers);
        this.isConstant = isConstant;
        this.id = id;
        this.dataType = dataType;
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        ClassDesc classDesc = EnvironmentUtils.resolveClassDesc(environment, dataType.getClassDesc());
        DataType dataType = DataType.of(classDesc, this.dataType.isNullable());

        if (environment instanceof FileEnvironment fileEnvironment) {
            int accessFlags = AccessFlag.STATIC.mask();
            if (isConstant) accessFlags |= AccessFlag.FINAL.mask();

            if (modifiers.contains(Modifiers.OPEN())) accessFlags |= AccessFlag.PUBLIC.mask();
            else accessFlags |= AccessFlag.PRIVATE.mask();

            fileEnvironment.declareVariable(id, dataType, isConstant, value);
            instructionsSet.withField(id, classDesc, accessFlags);
            return;
        }

        if (environment instanceof ClassEnvironment classEnvironment) {
            int accessFlags = 0;
            if (modifiers.contains(Modifiers.OPEN())) accessFlags |= AccessFlag.PUBLIC.mask();
            else if (modifiers.contains(Modifiers.PRIVATE())) accessFlags |= AccessFlag.PRIVATE.mask();
            else if (modifiers.contains(Modifiers.PROTECTED())) accessFlags |= AccessFlag.PROTECTED.mask();

            if (modifiers.contains(Modifiers.SHARED())) accessFlags |= AccessFlag.STATIC.mask();
            if (isConstant) accessFlags |= AccessFlag.FINAL.mask();

            classEnvironment.declareVariable(id, dataType, isConstant, value);
            instructionsSet.withField(id, classDesc, accessFlags);
            return;
        }

        if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) throw new RuntimeException("TODO " + environment.getClass().getName());
        VariableValue variableValue = variableDeclarationEnvironment.declareVariable(id, dataType, isConstant, value);

        if (value != null) {
            value.emit(instructionsSet, environment, this);
            ClassDesc valueType = value.getType(environment, this).getClassDesc();

            if (!valueType.equals(classDesc)) {
                if (NumberType.isNumberType(classDesc) && NumberType.isNumberType(valueType)) {
                    instructionsSet.convertToNumberType(valueType, classDesc);
                }

                else if (MiscUtils.isBoolean(classDesc) && MiscUtils.isBoolean(valueType)) {
                    instructionsSet.convertToBooleanType(valueType.isClassOrInterface(), classDesc.isClassOrInterface());
                }
            }
        }

        instructionsSet.storeLocal(classDesc, variableValue.getSlot());

        if (environment instanceof LocalVariableDeclarationEnvironment localDeclarationEnvironment) {
            if (localDeclarationEnvironment.getStartLabel() == null || localDeclarationEnvironment.getEndLabel() == null) return;

            instructionsSet.setLocalName(
                    variableValue.getSlot(), id, classDesc,
                    localDeclarationEnvironment.getStartLabel(), localDeclarationEnvironment.getEndLabel()
            );
        }
    }
}
