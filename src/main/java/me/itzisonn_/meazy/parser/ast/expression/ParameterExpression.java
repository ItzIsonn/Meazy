package me.itzisonn_.meazy.parser.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;

/**
 * Represents an expression that is used as parameter when defining callable statements
 */
@Getter
@EqualsAndHashCode
@NullMarked
public class ParameterExpression implements Expression {
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;

    /**
     * @param id         Id
     * @param dataType   Data type
     * @param isConstant Whether this parameter expression is constant
     */
    public ParameterExpression(String id, DataType dataType, boolean isConstant) {
        this.id = id;
        this.dataType = dataType;
        this.isConstant = isConstant;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {}

    @Override
    public ClassDesc getType(Environment environment, Statement parent) {
        return dataType.getClassDescriptor(environment);
    }
}