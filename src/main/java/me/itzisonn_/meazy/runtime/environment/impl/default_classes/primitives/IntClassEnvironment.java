package me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives;

import me.itzisonn_.meazy.parser.ast.Modifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.List;
import java.util.Set;

public class IntClassEnvironment extends ClassEnvironmentImpl {
    public IntClassEnvironment(Environment parent) {
        super(parent, true, "Int");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Int", false), this, Set.of(Modifiers.SHARED())) {
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
