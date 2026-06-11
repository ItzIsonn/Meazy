package me.itzisonn_.meazy.parser.ast.expression;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an expression that is used as parameter when defining callable statements
 */
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
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return dataType;
    }

    public String getId() {
        return this.id;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public boolean isConstant() {
        return this.isConstant;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ParameterExpression)) return false;
        final ParameterExpression other = (ParameterExpression) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$dataType = this.getDataType();
        final Object other$dataType = other.getDataType();
        if (this$dataType == null ? other$dataType != null : !this$dataType.equals(other$dataType)) return false;
        if (this.isConstant() != other.isConstant()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ParameterExpression;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $dataType = this.getDataType();
        result = result * PRIME + ($dataType == null ? 43 : $dataType.hashCode());
        result = result * PRIME + (this.isConstant() ? 79 : 97);
        return result;
    }
}