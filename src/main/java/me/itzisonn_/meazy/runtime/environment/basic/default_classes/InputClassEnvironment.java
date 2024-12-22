package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.*;

public class InputClassEnvironment extends BasicClassEnvironment {
    public InputClassEnvironment(Environment parent) {
        super(parent, true, "Input");


        declareFunction("read", new DefaultFunctionValue(new ArrayList<>(), DataTypes.STRING, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(Utils.SCANNER.next());
            }
        });

        declareFunction("readLine", new DefaultFunctionValue(new ArrayList<>(), DataTypes.STRING, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new StringValue(Utils.SCANNER.nextLine());
            }
        });

        declareFunction("readInt", new DefaultFunctionValue(new ArrayList<>(), DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new IntValue(Utils.SCANNER.nextInt());
            }
        });

        declareFunction("readFloat", new DefaultFunctionValue(new ArrayList<>(), DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(Utils.SCANNER.nextDouble());
            }
        });
    }
}
