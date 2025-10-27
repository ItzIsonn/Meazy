package me.itzisonn_.meazy.context;

import lombok.Getter;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;

import java.util.List;

/**
 * Represents runtime context
 */
@Getter
public class RuntimeContext {
    private final Interpreter interpreter;
    private final GlobalEnvironment globalEnvironment;

    public RuntimeContext() {
        interpreter = new Interpreter(this);
        globalEnvironment = Registries.GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue().create(this);

        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
            for (String lines : addon.getDatagenManager().getDatagenFilesLines("program")) {
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(lines);
                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(null, tokens);

                FileEnvironment fileEnvironment = Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment);
                globalEnvironment.addFileEnvironment(fileEnvironment);
            }
        }
    }
}
