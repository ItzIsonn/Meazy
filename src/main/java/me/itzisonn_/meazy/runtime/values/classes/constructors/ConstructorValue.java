package me.itzisonn_.meazy.runtime.values.classes.constructors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * ClassValue represents runtime constructor value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValue extends RuntimeValue<Object> {
    protected final List<CallArgExpression> args;
    protected final ConstructorDeclarationEnvironment parentEnvironment;
    protected final Set<Modifier> modifiers;

    /**
     * ConstructorValue constructor
     *
     * @param args Args of this ConstructorValue
     * @param parentEnvironment Parent of this ConstructorValue
     * @param modifiers AccessModifiers of this ConstructorValue
     */
    public ConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(null);
        this.args = args;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }
}
