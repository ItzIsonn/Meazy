package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;

import java.util.List;
import java.util.Set;

@Getter
public class ConstructorDeclarationStatement implements Statement {
    private final List<CallArgExpression> args;
    private final List<Statement> body;
    private final Set<AccessModifier> accessModifiers;

    public ConstructorDeclarationStatement(List<CallArgExpression> args, List<Statement> body, Set<AccessModifier> accessModifiers) {
        this.args = args;
        this.body = body;
        this.accessModifiers = accessModifiers;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder accessModifiersBuilder = new StringBuilder();
        for (AccessModifier accessModifier : accessModifiers) {
            accessModifiersBuilder.append(accessModifier.getId()).append(" ");
        }

        StringBuilder argsBuilder = new StringBuilder();
        for (int i = 0; i < args.size(); i++) {
            argsBuilder.append(args.get(i).toCodeString());
            if (i != args.size() - 1) argsBuilder.append(", ");
        }

        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return accessModifiersBuilder + "constructor(" + argsBuilder + ") {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
    }
}
