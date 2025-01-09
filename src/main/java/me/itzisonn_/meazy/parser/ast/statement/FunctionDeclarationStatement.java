package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;

import java.util.List;
import java.util.Set;

@Getter
public class FunctionDeclarationStatement implements Statement {
    private final String id;
    private final List<CallArgExpression> args;
    private final List<Statement> body;
    private final DataType returnDataType;
    private final Expression arraySize;
    private final Set<AccessModifier> accessModifiers;

    public FunctionDeclarationStatement(String id, List<CallArgExpression> args, List<Statement> body, DataType returnDataType, Expression arraySize, Set<AccessModifier> accessModifiers) {
        this.id = id;
        this.args = args;
        this.body = body;
        this.returnDataType = returnDataType;
        this.arraySize = arraySize;
        this.accessModifiers = accessModifiers;
    }

    @Override
    public String toCodeString(int offset) {
        StringBuilder accessModifiersBuilder = new StringBuilder();
        for (AccessModifier accessModifier : accessModifiers) {
            accessModifiersBuilder.append(accessModifier.getId()).append(" ");
        }

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            argsBuilder.append(args.get(i).toCodeString(0));
            if (i != args.size() - 1) argsBuilder.append(", ");
        }

        String returnDataTypeString = returnDataType == null ? "" : ":" + returnDataType.getName();

        String arrayString;
        if (arraySize == null) arrayString = "";
        else if (arraySize instanceof NullLiteral) arrayString = "[]";
        else arrayString = "[" + arraySize.toCodeString(0) + "]";

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return accessModifiersBuilder + "function " + id + "(" + argsBuilder + ")" + returnDataTypeString + arrayString + " {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
    }
}
