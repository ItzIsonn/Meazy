package me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitive;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.value.number.LongValue;

import java.util.List;
import java.util.Set;

public class LongClassEnvironment extends ClassEnvironmentImpl {
    public LongClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Long");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("valueOf", List.of(
                new CallArgExpression("object", new DataType("Any", false), true)),
                new DataType("Long", true), this, Set.of(Modifiers.SHARED())) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String value = functionArgs.getFirst().getFinalValue().toString();
                try {
                    return new LongValue(Long.parseLong(value.replaceAll("\\.0$", "")));
                }
                catch (NumberFormatException ignore) {
                    return new NullValue();
                }
            }
        });
    }
}
