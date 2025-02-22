package me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.Modifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;

import java.util.List;
import java.util.Set;

public class DoubleClassEnvironment extends ClassEnvironmentImpl {
    public DoubleClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Double");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Double", true), this, Set.of(Modifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return new DoubleValue(Double.parseDouble(value));
                }
                catch (NumberFormatException ignore) {
                    return new NullValue();
                }
            }
        });
    }
}
