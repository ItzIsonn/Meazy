package me.itzisonn_.meazy.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class ClassValueImpl extends ModifierableValueImpl implements ClassValue {
    protected final ClassEnvironment environment;
    protected final List<Statement> body;

    public ClassValueImpl(ClassEnvironment environment, List<Statement> body) {
        super(environment.getModifiers());

        this.environment = environment;
        this.body = body;
    }



    @Override
    public String getId() {
        return environment.getId();
    }

    @Override
    public Set<Modifier> getModifiers() {
        return environment.getModifiers();
    }

    @Override
    public Set<String> getBaseClasses() {
        ClassDesc baseClass = environment.getBaseClass();
        if (baseClass == null) return new HashSet<>();
        return new HashSet<>(Set.of(baseClass.descriptorString()));
    }

    @Override
    public boolean isInterface() {
        return environment.isInterface();
    }



    @Override
    public boolean isAccessible(Environment environment) {
        Identifier identifier = new ClassIdentifier(getId());

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getEnvironment().getParent(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
