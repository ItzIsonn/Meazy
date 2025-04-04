package me.itzisonn_.meazy.runtime.value.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents runtime function value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class FunctionValue extends RuntimeValue<Object> {
    /**
     * FunctionValue' id
     */
    protected final String id;
    /**
     * FunctionValue's args
     */
    protected final List<CallArgExpression> args;
    /**
     * Which DataType should this FunctionValue return
     */
    protected final DataType returnDataType;
    /**
     * FunctionValue's parent environment
     */
    protected final FunctionDeclarationEnvironment parentEnvironment;
    /**
     * FunctionValue's modifiers
     */
    protected final Set<Modifier> modifiers;
    /**
     * Whether this FunctionValue is overridden
     */
    protected boolean isOverridden = false;

    /**
     * FunctionValue constructor
     *
     * @param id FunctionValue' id
     * @param args FunctionValue's args
     * @param returnDataType Which DataType should this FunctionValue return or null
     * @param parentEnvironment FunctionValue's parent environment
     * @param modifiers FunctionValue's modifiers
     */
    public FunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(null);
        this.id = id;
        this.args = args;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }

    /**
     * Sets this FunctionValue overridden
     */
    public void setOverridden() {
        if (!(parentEnvironment instanceof ClassEnvironment)) throw new RuntimeException("Can't make function overridden because it's not inside a class");
        isOverridden = true;
    }

    /**
     * Copies this FunctionValue with given parent environment
     *
     * @param parentEnvironment New parent of this FunctionValue
     * @return Copy of this FunctionValue
     */
    public abstract FunctionValue copy(FunctionDeclarationEnvironment parentEnvironment);

    /**
     * Returns whether this Function has same id, args and returnDataType
     *
     * @param o Object to compare
     * @return Whether this FunctionValue is like o
     */
    public boolean isLike(Object o) {
        if (o == this) return true;
        else if (!(o instanceof FunctionValue other)) return false;
        else if (!super.equals(o)) return false;
        else {
            Object this$id = this.getId();
            Object other$id = other.getId();
            if (this$id == null) {
                if (other$id != null) return false;
            }
            else if (!this$id.equals(other$id)) return false;

            Object this$args = this.getArgs();
            Object other$args = other.getArgs();
            if (this$args == null) {
                if (other$args != null) return false;
            }
            else if (!this$args.equals(other$args)) return false;

            Object this$returnDataType = this.getReturnDataType();
            Object other$returnDataType = other.getReturnDataType();
            if (this$returnDataType == null) {
                return other$returnDataType == null;
            }
            else return this$returnDataType.equals(other$returnDataType);
        }
    }
}
