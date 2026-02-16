package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ParameterExpressionParsingFunction extends AbstractParsingFunction<ParameterExpression> {
    public ParameterExpressionParsingFunction() {
        super("parameter_expression");
    }

    @Override
    public ParameterExpression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        if (!parser.getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            throw new UnexpectedTokenException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.expected.start_expression", "variable", "parameter"));
        }

        boolean isConstant = parser.getCurrentAndNext().getValue().equals("val");
        String id = parser.getCurrentAndNext(TokenTypes.ID(), Text.translatable("meazy:parser.expected.after_keyword", "id", "variable")).getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        return new ParameterExpression(id, dataType == null ? DataType.anyNullable() : dataType, isConstant);
    }
}
