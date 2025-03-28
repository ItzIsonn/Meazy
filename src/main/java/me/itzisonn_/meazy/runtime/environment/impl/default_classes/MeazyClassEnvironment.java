package me.itzisonn_.meazy.runtime.environment.impl.default_classes;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.ClassEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.collections.ListClassEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.value.function.DefaultFunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeazyClassEnvironment extends ClassEnvironmentImpl {
    public MeazyClassEnvironment(ClassDeclarationEnvironment parent) {
        super(parent, true, "Meazy");


        declareVariable(new VariableValue(
                "VERSION",
                new DataType("String", false),
                new StringValue(MeazyMain.VERSION),
                true,
                Set.of(Modifiers.SHARED()),
                false));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of(Modifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getAddons", List.of(), new DataType("List", false), this, Set.of(Modifiers.SHARED())) {
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
