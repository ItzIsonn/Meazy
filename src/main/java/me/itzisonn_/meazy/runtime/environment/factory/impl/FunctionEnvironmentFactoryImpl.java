package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.FunctionEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.FunctionEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class FunctionEnvironmentFactoryImpl implements FunctionEnvironmentFactory {
    @Override
    public FunctionEnvironment create(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, String id, List<ParameterExpression> parameters, @Nullable DataType returnDataType, boolean isShared, Set<Modifier> modifiers) {
        return new FunctionEnvironmentImpl(parent, startLabel, endLabel, id, parameters, returnDataType, isShared, modifiers);
    }
}
