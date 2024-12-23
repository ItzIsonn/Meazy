package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.Set;

@Getter
public class VariableDeclarationStatement implements Statement {
    private final String id;
    private final DataType dataType;
    private final Expression value;
    private final boolean isConstant;
    private final Set<AccessModifier> accessModifiers;

    public VariableDeclarationStatement(String id, DataType dataType, Expression value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        this.id = id;
        if (dataType != null) this.dataType = dataType;
        else this.dataType = DataTypes.ANY();
        this.value = value;
        this.isConstant = isConstant;
        this.accessModifiers = accessModifiers;
    }

    @Override
    public String toCodeString(int offset) {
        StringBuilder accessModifiersBuilder = new StringBuilder();
        for (AccessModifier accessModifier : accessModifiers) {
            accessModifiersBuilder.append(accessModifier.getId()).append(" ");
        }

        String declareString = isConstant ? "val" : "var";
        String equals = value == null ? "" : " = " + value.toCodeString(0);

        return accessModifiersBuilder + declareString + " " + id + ":" + dataType.getName() + equals;
    }
}
