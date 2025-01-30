package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;

import java.util.List;
import java.util.Set;

@Getter
public class FunctionDeclarationStatement implements Statement {
    private final Set<String> accessModifiers;
    private final String id;
    private final List<CallArgExpression> args;
    private final List<Statement> body;
    private final String returnDataType;

    public FunctionDeclarationStatement(Set<String> accessModifiers, String id, List<CallArgExpression> args, List<Statement> body, String returnDataType) {
        this.accessModifiers = accessModifiers;
        this.id = id;
        this.args = args;
        this.body = body;
        this.returnDataType = returnDataType;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder accessModifiersBuilder = new StringBuilder();
        for (String accessModifier : accessModifiers) {
            accessModifiersBuilder.append(accessModifier).append(" ");
        }

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            argsBuilder.append(args.get(i).toCodeString(0));
            if (i != args.size() - 1) argsBuilder.append(", ");
        }

        String returnDataTypeString = returnDataType == null ? "" : ":" + returnDataType;

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return accessModifiersBuilder + "function " + id + "(" + argsBuilder + ")" + returnDataTypeString + " {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
    }
}
