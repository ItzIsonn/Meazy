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
 * Represents runtime constructor value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValue extends RuntimeValue<Object> {
    /**
     * ConstructorValue's args
     */
    protected final List<CallArgExpression> args;
    /**
     * ConstructorValue's parent environment
     */
    protected final ConstructorDeclarationEnvironment parentEnvironment;
    /**
     * ConstructorValue's modifiers
     */
    protected final Set<Modifier> modifiers;

    /**
     * ConstructorValue constructor
     *
     * @param args ConstructorValue's args
     * @param parentEnvironment ConstructorValue's parent environment
     * @param modifiers ConstructorValue's modifiers
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
     * Copies this ConstructorValue with given parent environment
     *
     * @param parentEnvironment New parent of this ConstructorValue
     * @return Copy of this ConstructorValue
     */
    public abstract ConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment);
}
