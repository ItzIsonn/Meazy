package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;

public class BasicEnvironment implements Environment {
    @Getter
    protected final Environment parent;
    protected boolean isShared;

    public BasicEnvironment(Environment parent, boolean isShared) {
        this.parent = parent;
        this.isShared = isShared;
    }

    public BasicEnvironment(Environment parent) {
        this.parent = parent;
        this.isShared = false;
    }

    public boolean isShared() {
        if (isShared) return true;
        return parent.isShared();
    }
}