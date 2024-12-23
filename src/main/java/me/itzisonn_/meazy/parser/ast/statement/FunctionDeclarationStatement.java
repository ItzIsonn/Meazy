package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class FunctionDeclarationStatement implements Statement {
    private final String id;
    private final ArrayList<CallArgExpression> args;
    private final ArrayList<Statement> body;
    private final DataType returnDataType;
    private final Set<AccessModifier> accessModifiers;

    public FunctionDeclarationStatement(String id, ArrayList<CallArgExpression> args, ArrayList<Statement> body, DataType returnDataType, Set<AccessModifier> accessModifiers) {
        this.id = id;
        this.args = args;
        this.body = body;
        this.returnDataType = returnDataType;
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

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return accessModifiersBuilder + "function " + id + "(" + argsBuilder + ")" + returnDataTypeString + " {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
    }
}
