package me.itzisonn_.meazy.runtime.environment.basic.default_classes.primitives;

import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;

import java.util.List;
import java.util.Set;

public class FloatClassEnvironment extends BasicClassEnvironment {
    public FloatClassEnvironment(Environment parent) {
        super(parent, true, "Float");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Float", false), this, Set.of("shared")) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                try {
                    return new DoubleValue(Float.parseFloat(value.toString()));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidSyntaxException("Can't convert " + value + " to Float");
                }
            }
        });
    }
}
