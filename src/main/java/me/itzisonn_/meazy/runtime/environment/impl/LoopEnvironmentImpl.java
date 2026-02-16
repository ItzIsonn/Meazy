package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class LoopEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements LoopEnvironment {
    public LoopEnvironmentImpl(Environment parent, UUID startLabel, UUID endLabel) {
        super(parent, startLabel, endLabel);
    }

    @Override
    public UUID getStartLabel() {
        UUID startLabel = super.getStartLabel();
        if (startLabel == null) throw new NullPointerException("StartLabel is null");
        return startLabel;
    }

    @Override
    public UUID getEndLabel() {
        UUID endLabel = super.getEndLabel();
        if (endLabel == null) throw new NullPointerException("EndLabel is null");
        return endLabel;
    }
}