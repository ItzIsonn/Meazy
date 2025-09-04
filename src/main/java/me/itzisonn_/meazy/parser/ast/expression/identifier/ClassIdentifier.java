package me.itzisonn_.meazy.parser.ast.expression.identifier;

public class ClassIdentifier extends Identifier {
    public ClassIdentifier(String id) {
        super(id);
    }

    @Override
    public String toCodeString() {
        return id;
    }
}