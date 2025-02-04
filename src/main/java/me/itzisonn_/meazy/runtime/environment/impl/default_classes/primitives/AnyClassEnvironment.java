package me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives;

import me.itzisonn_.meazy.parser.ast.Modifiers;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;

import java.util.List;
import java.util.Set;

public class AnyClassEnvironment extends ClassEnvironmentImpl {
    public AnyClassEnvironment(Environment parent) {
        super(parent, true, "Any");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });
    }
}
