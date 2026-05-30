package me.itzisonn_.meazy.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ConstructorClassIdentifier;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
@NullMarked
public class ConstructorValueImpl implements ConstructorValue {
    protected final List<ParameterExpression> parameters;
    protected final List<Statement> body;
    protected final ConstructorEnvironment environment;

    public ConstructorValueImpl(List<ParameterExpression> parameters, List<Statement> body, ConstructorEnvironment environment) {
        this.parameters = parameters;
        this.body = body;
        this.environment = environment;
    }


    @Override
    public Set<Modifier> getModifiers() {
        return environment.getModifiers();
    }

    @Override
    public boolean isAccessible(Environment environment) {
        if (!(getEnvironment().getParent() instanceof ClassEnvironment classEnvironment)) return true;
        Identifier identifier = new ConstructorClassIdentifier(classEnvironment.getId());

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getEnvironment().getParent(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
