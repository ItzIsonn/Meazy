package me.itzisonn_.meazy.runtime.environment.impl.default_classes;

import me.itzisonn_.meazy.parser.Modifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.*;

public class InputClassEnvironment extends ClassEnvironmentImpl {
    private static final Scanner SCANNER = new Scanner(System.in);

    public InputClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Input");


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("read", List.of(), new DataType("String", false), this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(SCANNER.next());
            }
        });

        declareFunction(new DefaultFunctionValue("readLine", List.of(), new DataType("String", false), this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(SCANNER.nextLine());
            }
        });

        declareFunction(new DefaultFunctionValue("readInt", List.of(), new DataType("String", false), this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new IntValue(SCANNER.nextInt());
            }
        });

        declareFunction(new DefaultFunctionValue("readFloat", List.of(), new DataType("String", false), this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(SCANNER.nextDouble());
            }
        });
    }
}
