package me.itzisonn_.meazy.runtime.environment.impl;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LoopEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements LoopEnvironment {
    public LoopEnvironmentImpl(Environment parent, Uuid startLabel, Uuid endLabel) {
        super(parent, startLabel, endLabel);
    }

    @Override
    public Uuid getStartLabel() {
        Uuid startLabel = super.getStartLabel();
        if (startLabel == null) throw new NullPointerException("StartLabel is null");
        return startLabel;
    }

    @Override
    public Uuid getEndLabel() {
        Uuid endLabel = super.getEndLabel();
        if (endLabel == null) throw new NullPointerException("EndLabel is null");
        return endLabel;
    }
}