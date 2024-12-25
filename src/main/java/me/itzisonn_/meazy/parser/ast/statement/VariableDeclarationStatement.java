package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;

import java.util.Set;

@Getter
public class VariableDeclarationStatement implements Statement {
    private final String id;
    private final Expression arraySize;
    private final DataType dataType;
    private final Expression value;
    private final boolean isConstant;
    private final Set<AccessModifier> accessModifiers;

    public VariableDeclarationStatement(String id, Expression arraySize, DataType dataType, Expression value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        this.id = id;
        this.arraySize = arraySize;
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

        String arrayString;
        if (arraySize == null) arrayString = "";
        else if (arraySize instanceof NullLiteral) arrayString = "[]";
        else arrayString = "[" + arraySize.toCodeString(0) + "]";

        String equalsString = value == null ? "" : " = " + value.toCodeString(0);

        return accessModifiersBuilder + declareString + " " + id + arrayString + ":" + dataType.getName() + equalsString;
    }
}
