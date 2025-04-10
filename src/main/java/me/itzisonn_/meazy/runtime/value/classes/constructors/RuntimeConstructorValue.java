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
 * Represents runtime constructor value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeConstructorValue extends ConstructorValue {
    /**
     * Body
     */
    private final List<Statement> body;

    /**
     * @param args Args
     * @param body = Body
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, body, parentEnvironment or modifiers is null
     */
    public RuntimeConstructorValue(List<CallArgExpression> args, List<Statement> body,
                                   ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(args, parentEnvironment, modifiers);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;
    }

    @Override
    public final ConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment) {
        return new RuntimeConstructorValue(args, new ArrayList<>(body), parentEnvironment, modifiers);
    }
}
