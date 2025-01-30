package me.itzisonn_.meazy.runtime.environment.basic.default_classes.primitives;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BooleanClassEnvironment extends BasicClassEnvironment {
    public BooleanClassEnvironment(Environment parent) {
        super(parent, true, "Boolean");


        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", new ArrayList<>(List.of(new CallArgExpression("object", "Any", true))), "Boolean", this, Set.of("shared")) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                if (value.equals("true") || value.equals("false")) return new BooleanValue(Boolean.parseBoolean(value));
                throw new InvalidSyntaxException("Can't convert " + value + " to Boolean");
            }
        });
    }
}
