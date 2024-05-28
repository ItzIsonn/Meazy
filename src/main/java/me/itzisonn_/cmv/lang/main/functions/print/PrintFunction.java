package me.itzisonn_.cmv.lang.main.functions.print;

import me.itzisonn_.cmv.lang.main.FunctionVariable;
import me.itzisonn_.cmv.lang.main.functions.DefaultFunction;
import me.itzisonn_.cmv.lang.types.Type;

import java.util.ArrayList;
import java.util.List;

public class PrintFunction extends DefaultFunction {
    public PrintFunction() {
        super("print", new ArrayList<>(List.of(new FunctionVariable("string", Type.ANY, true))), null);
    }

    @Override
    public void run(ArrayList<Object> paramsValues) {
        checkValues(paramsValues);

        System.out.print(paramsValues.getFirst());
    }
}