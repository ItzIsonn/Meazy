package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EmptyClassEnvironment extends BasicClassEnvironment {
    public EmptyClassEnvironment(Environment parent, String id) {
        super(parent, true, id);


        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of(AccessModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });
    }
}
