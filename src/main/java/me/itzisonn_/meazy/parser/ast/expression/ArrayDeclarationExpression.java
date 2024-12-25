package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ArrayDeclarationExpression implements Expression {
    private final ArrayList<Expression> values;

    public ArrayDeclarationExpression(ArrayList<Expression> values) {
        this.values = values;
    }

    @Override
    public String toCodeString() {
        StringBuilder valuesBuilder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            valuesBuilder.append(values.get(i).toCodeString());
            if (i < values.size() - 1) valuesBuilder.append(", ");
        }

        return "{" + valuesBuilder + "}";
    }
}
