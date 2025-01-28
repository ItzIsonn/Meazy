package me.itzisonn_.meazy.runtime.environment.basic;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.*;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.ClassValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.functions.FunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.*;

public class BasicGlobalEnvironment extends BasicVariableDeclarationEnvironment implements GlobalEnvironment {
    private final List<FunctionValue> functions;
    private final List<ClassValue> classes;

    public BasicGlobalEnvironment() {
        super(null, false);
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    @Override
    public void declareClass(String id, ClassValue value) {
        ClassValue classValue = getClass(id);
        if (classValue != null) throw new InvalidSyntaxException("Class with id " + id + " already exists!");
        classes.add(value);
    }

    @Override
    public List<ClassValue> getClasses() {
        return new ArrayList<>(classes);
    }

    @Override
    public void declareFunction(FunctionValue value) {
        List<CallArgExpression> args = value.getArgs();

        main:
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(value.getId())) {
                List<CallArgExpression> callArgExpressions = functionValue.getArgs();

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    CallArgExpression callArgExpression = callArgExpressions.get(i);
                    if (!callArgExpression.getDataType().equals(args.get(i).getDataType())) continue main;
                }

                throw new InvalidSyntaxException("Function with id " + value.getId() + " already exists!");
            }
        }

        functions.add(value);
    }

    @Override
    public FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        if (getFunction(id, args) != null) return this;
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(id)) throw new InvalidIdentifierException("Function with id " + id + " exists but doesn't match args!");
        }
        throw new InvalidIdentifierException("Function with id " + id + " doesn't exist!");
    }

    @Override
    public List<FunctionValue> getFunctions() {
        return new ArrayList<>(functions);
    }

    @Override
    public boolean isShared() {
        return isShared;
    }

    public void init() {
        declareClass("any", new DefaultClassValue(new EmptyClassEnvironment(this, "any")) {
            @Override
            public boolean isMatches(Object value) {
                return true;
            }
        });

        declareClass("boolean", new DefaultClassValue(new EmptyClassEnvironment(this, "boolean")) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Boolean || value instanceof BooleanLiteral || value instanceof BooleanValue) return true;
                return value.toString().equals("true") || value.toString().equals("false");
            }
        });

        declareClass("int", new DefaultClassValue(new EmptyClassEnvironment(this, "int")) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Integer || value instanceof IntValue) return true;
                if (value instanceof NumberLiteral numberLiteral) {
                    return numberLiteral.isInt();
                }
                try {
                    Integer.parseInt(value.toString());
                    return true;
                }
                catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass("float", new DefaultClassValue(new EmptyClassEnvironment(this, "float")) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Float || value instanceof NumberLiteral || value instanceof NumberValue) return true;
                try {
                    Float.parseFloat(value.toString());
                    return true;
                }
                catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass("string", new DefaultClassValue(new EmptyClassEnvironment(this, "string")));


        declareFunction(new DefaultFunctionValue("print", new ArrayList<>(List.of(new CallArgExpression("value", "any", true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                System.out.print(functionArgs.getFirst().getFinalValue());
                return null;
            }
        });

        declareFunction( new DefaultFunctionValue("println", new ArrayList<>(List.of(new CallArgExpression("value", "any", true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                System.out.println(functionArgs.getFirst().getFinalValue());
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("range",
                new ArrayList<>(List.of(new CallArgExpression("begin", "int", true), new CallArgExpression("end", "int", true))),
                "List", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                int begin;
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue) {
                    begin = beginValue.getValue();
                }
                else throw new InvalidArgumentException("Begin must be int");

                int end;
                if (functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue) {
                    end = endValue.getValue();
                }
                else throw new InvalidArgumentException("End must be int");

                List<RuntimeValue<?>> list = new ArrayList<>();
                for (int i = begin; i < end; i++) {
                    list.add(new IntValue(i));
                }
                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
            }
        });


        declareClass("Input", new DefaultClassValue(new InputClassEnvironment(this)));
        declareClass("List", new DefaultClassValue(new ListClassEnvironment(this)));
        declareClass("Math", new DefaultClassValue(new MathClassEnvironment(this)));
        declareClass("Random", new DefaultClassValue(new RandomClassEnvironment(this)));
        declareClass("Meazy", new DefaultClassValue(new MeazyClassEnvironment(this)));
    }
}