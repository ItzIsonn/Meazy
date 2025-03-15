package me.itzisonn_.meazy.runtime.value.classes.constructors;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

@EqualsAndHashCode(callSuper = true)
public class BaseClassIdValue extends RuntimeValue<String> {
    public BaseClassIdValue(String id) {
        super(id);
    }
}
