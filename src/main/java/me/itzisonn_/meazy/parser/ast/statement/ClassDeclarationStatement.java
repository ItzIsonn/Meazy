package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.Modifier;

import java.util.List;
import java.util.Set;

@Getter
public class ClassDeclarationStatement extends ModifierStatement implements Statement {
    private final String id;
    private final List<Statement> body;

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, List<Statement> body) {
        super(modifiers);
        this.id = id;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder bodyBuilder = new StringBuilder();
        for (Statement statement : body) {
            bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
        }

        return super.toCodeString(0) + "class " + id + " {\n" + bodyBuilder + Utils.getOffset(offset -1) + "}";
    }
}
