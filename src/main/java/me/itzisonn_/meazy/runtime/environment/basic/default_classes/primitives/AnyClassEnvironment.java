package me.itzisonn_.meazy.runtime.environment.basic.default_classes.primitives;

import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;

import java.util.List;
import java.util.Set;

public class AnyClassEnvironment extends BasicClassEnvironment {
    public AnyClassEnvironment(Environment parent) {
        super(parent, true, "Any");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });
    }
}
