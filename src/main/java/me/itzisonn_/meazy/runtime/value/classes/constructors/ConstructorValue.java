package me.itzisonn_.meazy.runtime.value.classes.constructors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents constructor value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValue extends RuntimeValue<Object> {
    /**
     * Args
     */
    protected final List<CallArgExpression> args;
    /**
     * Parent environment
     */
    protected final ConstructorDeclarationEnvironment parentEnvironment;
    /**
     * Modifiers
     */
    protected final Set<Modifier> modifiers;

    /**
     * @param args Args
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public ConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(null);

        if (args == null) throw new NullPointerException("Args can't be null");
        if (parentEnvironment == null) throw new NullPointerException("ParentEnvironment can't be null");
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");

        this.args = args;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }

    /**
     * Copies this constructor with given parent environment
     *
     * @param parentEnvironment New parent of this constructor
     * @return Copy of this constructor
     */
    public abstract ConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment);
}
