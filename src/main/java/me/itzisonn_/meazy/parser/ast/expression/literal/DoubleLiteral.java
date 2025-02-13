package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.text.DecimalFormat;
import java.text.NumberFormat;

@Getter
public class DoubleLiteral implements Expression {
    private final double value;

    public DoubleLiteral(double value) {
        this.value = value;
    }

    @Override
    public String toCodeString() {
        return NUMBER_FORMAT.format(value);
    }

    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat();

    static {
        NUMBER_FORMAT.setGroupingUsed(false);
        NUMBER_FORMAT.setMinimumFractionDigits(1);
    }
}
