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
 * Represents function value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class FunctionValue extends RuntimeValue<Object> {
    /**
     * Id
     */
    protected final String id;
    /**
     * Args
     */
    protected final List<CallArgExpression> args;
    /**
     * Which DataType should this function return
     */
    protected final DataType returnDataType;
    /**
     * Parent environment
     */
    protected final FunctionDeclarationEnvironment parentEnvironment;
    /**
     * Modifiers
     */
    protected final Set<Modifier> modifiers;
    /**
     * Whether this function is overridden
     */
    protected boolean isOverridden = false;

    /**
     * @param id Id
     * @param args Args
     * @param returnDataType Which DataType should this function return or null
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
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
     * Makes this function overridden (can't be undone)
     */
    public void setOverridden() {
        if (!(parentEnvironment instanceof ClassEnvironment)) throw new RuntimeException("Can't make function overridden because it's not inside a class");
        isOverridden = true;
    }

    /**
     * Returns whether this function has same id, args and returnDataType
     *
     * @param o Object to compare
     * @return Whether this function is like o
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
