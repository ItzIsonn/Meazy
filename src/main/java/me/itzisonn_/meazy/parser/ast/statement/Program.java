package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;

import java.util.List;

@Getter
public class Program implements Statement {
    private final String version;
    private final List<Statement> body;

    public Program(String version, List<Statement> body) {
        this.version = version;
        this.body = body;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            bodyBuilder.append(Utils.getOffset(offset)).append(body.get(i).toCodeString(offset + 1));
            if (i != body.size() - 1) bodyBuilder.append("\n");
        }

        return bodyBuilder.toString();
    }
}