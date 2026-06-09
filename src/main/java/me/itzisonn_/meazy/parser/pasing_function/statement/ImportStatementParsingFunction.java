package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ImportStatementParsingFunction extends AbstractParsingFunction<ImportStatement> {
    public ImportStatementParsingFunction() {
        super("import_statement");
    }

    @Override
    public ImportStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        parser.next(TokenTypes.IMPORT(), TextKt.translatable("meazy:parser.expected.keyword", "import"));

        StringBuilder name = new StringBuilder(parser.getCurrentAndNext(
                TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "import")).getValue()
        );

        while (parser.getCurrent().getType().equals(TokenTypes.DOT())) {
            parser.next();
            name.append(".");
            name.append(parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue());
        }

        return new ImportStatement(name.toString());
    }
}
