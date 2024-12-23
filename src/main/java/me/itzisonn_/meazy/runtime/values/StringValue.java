package me.itzisonn_.meazy.runtime.values;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.StringClassEnvironment;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;

public class StringValue extends DefaultClassValue {
    public StringValue(String value) {
        super(new StringClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), value));
    }

    public StringValue(StringClassEnvironment stringClassEnvironment) {
        super(stringClassEnvironment);
    }

    @Override
    public String getValue() {
        return getClassEnvironment().getVariable("value").getValue().getFinalValue().toString();
    }
}