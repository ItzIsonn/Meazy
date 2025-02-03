package me.itzisonn_.meazy.runtime.environment.impl.default_classes;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.*;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListClassEnvironment extends ClassEnvironmentImpl {
    public ListClassEnvironment(Environment parent) {
        this(parent, new ArrayList<>());
    }

    public ListClassEnvironment(Environment parent, List<RuntimeValue<?>> list) {
        super(parent, false, "List");


        declareVariable(new VariableValue(
                "value",
                new DataType("Any", false),
                new InnerListValue(list),
                false,
                Set.of("private"),
                false,
                this));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        declareConstructor(new DefaultConstructorValue(List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(constructorArgs.getFirst().getFinalRuntimeValue());
            }
        });


        declareFunction(new DefaultFunctionValue("getSize", List.of(), new DataType("Int", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get size of non-list value");

                return new IntValue(listValue.getValue().size());
            }
        });


        declareFunction(new DefaultFunctionValue("add", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                listValue.getValue().add(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("add",
                List.of(new CallArgExpression("element", new DataType("Any", true), true), new CallArgExpression("pos", new DataType("Int", false), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't add element to non-list value");

                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't add element to non-int pos");
                listValue.getValue().add(intValue.getValue(), functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't remove element from non-int pos");
                listValue.getValue().remove(intValue.getValue().intValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("remove", List.of(
                new CallArgExpression("element", new DataType("Any", true), true)),
                null, this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't remove element to non-list value");

                listValue.getValue().remove(functionArgs.getFirst().getFinalRuntimeValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("get", List.of(
                new CallArgExpression("pos", new DataType("Int", false), true)),
                new DataType("Any", true), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't get element from non-list value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get element from non-int pos");
                return new RuntimeValue<>(listValue.getValue().get(intValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("toString", List.of(), new DataType("String", false), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerListValue listValue)) throw new InvalidSyntaxException("Can't convert non-list value to string");

                return new StringValue(Utils.unpackRuntimeValuesList(listValue.getValue()).toString());
            }
        });
    }

    public static class InnerListValue extends RuntimeValue<List<RuntimeValue<?>>> {
        private InnerListValue(List<RuntimeValue<?>> value) {
            super(value);
        }
    }
}