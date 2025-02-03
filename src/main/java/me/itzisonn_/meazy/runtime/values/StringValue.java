package me.itzisonn_.meazy.runtime.values;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.StringClassEnvironment;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;

public class StringValue extends DefaultClassValue {
    public StringValue(String value) {
        super(new StringClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), value));
    }

    public StringValue(StringClassEnvironment stringClassEnvironment) {
        super(stringClassEnvironment);
    }

    @Override
    public String getValue() {
        return getEnvironment().getVariable("value").getValue().getFinalValue().toString();
    }

    @Override
    public String toString() {
        return getValue();
    }
}