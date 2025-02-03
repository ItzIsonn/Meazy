package me.itzisonn_.meazy.runtime.environment.impl.default_classes;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addons.Addon;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.VariableValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeazyClassEnvironment extends ClassEnvironmentImpl {
    public MeazyClassEnvironment(Environment parent) {
        super(parent, true, "Meazy");


        declareVariable(new VariableValue(
                "VERSION",
                new DataType("String", false),
                new StringValue(MeazyMain.VERSION),
                true,
                Set.of("shared"),
                false,
                this));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getAddons", List.of(), new DataType("List", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                List<RuntimeValue<?>> addons = new ArrayList<>();
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    addons.add(new StringValue(addon.getAddonInfo().getId()));
                }

                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), addons));
            }
        });
    }
}
