package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.program.Program;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy.parser.ast.statement.RequireStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class ProgramParsingFunction extends AbstractParsingFunction<Program> {
    public ProgramParsingFunction() {
        super("program");
    }

    @Override
    public Program parse(ParsingContext context, @Nullable Object... extra) {
        File file;
        if (extra.length == 0) throw new IllegalArgumentException("Expected file as extra argument");
        if (extra[0] == null) file = null;
        else if (extra[0] instanceof File fileArg) file = fileArg;
        else throw new IllegalArgumentException("Expected file as extra argument");

        Parser parser = context.getParser();
        parser.moveOverOptionalNewLines();

        Map<String, @Nullable Version> requiredAddons = new HashMap<>();
        List<Statement> body = new ArrayList<>();

        Statement headerStatement;

        while (true) {
            try {
                headerStatement = parser.parse(MeazyMain.getDefaultIdentifier("header_statement"), Statement.class);
            }
            catch (UnexpectedTokenException e) {
                break;
            }

            parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy:parser.expected", "new_line"));
            parser.moveOverOptionalNewLines();

            if (headerStatement instanceof RequireStatement requireStatement) {
                requiredAddons.put(requireStatement.getId(), requireStatement.getVersion());
            }
            else {
                body.add(headerStatement);
            }
        }

        parser.moveOverOptionalNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
            body.add(parser.parse(MeazyMain.getDefaultIdentifier("global_statement"), Statement.class));

            if (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                parser.next(TokenTypes.NEW_LINE(), Text.translatable("meazy:parser.expected", "new_line"));
                parser.moveOverOptionalNewLines();
            }
        }

        if (requiredAddons.isEmpty()) {
            for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                AddonInfo addonInfo = addon.getAddonInfo();
                requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
            }
        }

        return Registries.PROGRAM_FACTORY.getEntry().getValue().create(file, MeazyMain.VERSION, requiredAddons, body);
    }
}
