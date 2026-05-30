package me.itzisonn_.meazy.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
@NullMarked
public class FunctionValueImpl extends ModifierableValueImpl implements FunctionValue {
    protected final String id;
    protected final List<ParameterExpression> parameters;
    @Nullable
    @Setter
    protected DataType returnDataType;
    protected final FunctionEnvironment environment;
    protected boolean isOverridden = false;

    public FunctionValueImpl(String id, List<ParameterExpression> parameters, @Nullable DataType returnDataType, FunctionEnvironment environment, Set<Modifier> modifiers) {
        super(modifiers);

        this.id = id;
        this.parameters = parameters;
        this.returnDataType = returnDataType;
        this.environment = environment;
    }



    @Override
    public void setOverridden() {
        if (!(environment instanceof ClassEnvironment)) throw new RuntimeException("Can't make function overridden because it's not inside a class");
        isOverridden = true;
    }

    @Override
    public boolean isLike(Object o) {
        if (o == this) return true;
        if (!(o instanceof FunctionValue other)) return false;

        Object this$id = getId();
        Object other$id = other.getId();
        if (!this$id.equals(other$id)) return false;

        Object this$parameters = getParameters();
        Object other$parameters = other.getParameters();
        if (!this$parameters.equals(other$parameters)) return false;

        Object this$returnDataType = getReturnDataType();
        Object other$returnDataType = other.getReturnDataType();
        if (this$returnDataType == null) {
            return other$returnDataType == null;
        }
        else return this$returnDataType.equals(other$returnDataType);
    }



    @Override
    public boolean isAccessible(Environment environment) {
        Identifier identifier = new FunctionIdentifier(id);

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getEnvironment(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }
}
