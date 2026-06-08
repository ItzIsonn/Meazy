package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.util.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.util.logger.LogLevel;
import me.itzisonn_.meazy.parser.ast.program.Program;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Map;

@NullMarked
public class RunCommand extends AbstractCommand {
    public RunCommand() {
        super("run", List.of("<target_file>"));
    }

    @Override
    @Nullable
    public Text execute(String[] args) {
        File file = new File(args[0]);
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.getAbsolutePath()));
            return null;
        }

        String extension = FileUtils.getExtension(file);
        if (!extension.equals("mea")) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", extension));
            return null;
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.run.running", file.getAbsolutePath()));
        long startMillis = System.currentTimeMillis();

        List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));
        Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);

        Map<ClassDesc, byte[]> classes = Registries.COMPILE_PROGRAM_FUNCTION.getEntry().getValue().compile(program);
        Registries.RUN_PROGRAM_FUNCTION.getEntry().getValue().run(classes);

        long endMillis = System.currentTimeMillis();
        return Text.translatable("meazy:commands.run.info", (double) (endMillis - startMillis) / 1000);
    }
}
