package me.itzisonn_.meazy.runtime.environment.basic.default_classes.primitives;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntClassEnvironment extends BasicClassEnvironment {
    public IntClassEnvironment(Environment parent) {
        super(parent, true, "Int");


        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", new ArrayList<>(List.of(new CallArgExpression("object", "Any", true))), "Int", this, Set.of("shared")) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                try {
                    return new IntValue(Integer.parseInt(value.toString().replaceAll("\\.0$", "")));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidSyntaxException("Can't convert " + value + " to Int");
                }
            }
        });
    }
}
