package me.itzisonn_.meazy.runtime.value.classes.constructors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * RuntimeConstructorValue represents runtime constructor value created at runtime
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeConstructorValue extends ConstructorValue {
    private final List<Statement> body;

    /**
     * RuntimeConstructorValue constructor
     *
     * @param args Args of this RuntimeConstructorValue
     * @param parentEnvironment Parent of this RuntimeConstructorValue
     * @param modifiers Modifiers of this RuntimeConstructorValue
     * @param body Body of this RuntimeConstructorValue
     */
    public RuntimeConstructorValue(List<CallArgExpression> args, List<Statement> body, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(args, parentEnvironment, modifiers);
        this.body = body;
    }

    @Override
    public final ConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment) {
        return new RuntimeConstructorValue(args, new ArrayList<>(body), parentEnvironment, modifiers);
    }
}
