package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.modifier.Modifier;

import java.util.List;
import java.util.Set;

@Getter
public class ClassDeclarationStatement extends ModifierStatement implements Statement {
    private final String id;
    private final Set<String> baseClasses;
    private final List<Statement> body;

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body) {
        super(modifiers);
        this.id = id;
        this.baseClasses = baseClasses;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        String baseClassesString;
        if (!baseClasses.isEmpty()) {
            baseClassesString = " : " + String.join(", ", baseClasses);
        }
        else baseClassesString = "";

        String bodyString;
        if (!body.isEmpty()) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (Statement statement : body) {
                bodyBuilder.append(Utils.getOffset(offset)).append(statement.toCodeString(offset + 1)).append("\n");
            }
            bodyString = " {\n" + bodyBuilder + Utils.getOffset(offset - 1) + "}";
        }
        else bodyString = "";

        return super.toCodeString(0) + "class " + id + baseClassesString + bodyString;
    }
}
