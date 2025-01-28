package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addons.Addon;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MeazyClassEnvironment extends BasicClassEnvironment {
    public MeazyClassEnvironment(Environment parent) {
        super(parent, true, "Meazy");


        declareVariable("VERSION", "string", new StringValue(MeazyMain.VERSION), true, Set.of(AccessModifiers.SHARED()));
        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of(AccessModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getAddons", new ArrayList<>(), "List", this, Set.of(AccessModifiers.SHARED())) {
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
