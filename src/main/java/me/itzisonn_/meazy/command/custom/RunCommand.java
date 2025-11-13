package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.logging.LogLevel;
import me.itzisonn_.meazy.parser.ast.Program;

import java.io.File;
import java.util.List;

public class RunCommand extends AbstractCommand {
    public RunCommand() {
        super("run", List.of("<file_to_run>"));
    }

    @Override
    public Text execute(String[] args) {
        File file = new File(args[0]);
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.getAbsolutePath()));
            return null;
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.run.running", file.getAbsolutePath()));

        String extension = FileUtils.getExtension(file);
        long startMillis = System.currentTimeMillis();

        Program program;
        switch (extension) {
            case "mea" -> {
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));
                program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);
            }
            case "meac" -> {
                program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
                if (program == null) {
                    MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.failed_read", file.getAbsolutePath()));
                    return null;
                }
                if (MeazyMain.VERSION.isBefore(program.getVersion())) {
                    MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:commands.run.incompatible_version", program.getVersion(), MeazyMain.VERSION));
                    return null;
                }
                if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                    MeazyMain.LOGGER.log(LogLevel.WARNING, Text.translatable("meazy:commands.run.unsafe", program.getVersion(), MeazyMain.VERSION));
                }
                program.setFile(file);
            }
            default -> {
                MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", extension));
                return null;
            }
        }

        Registries.RUN_PROGRAM_FUNCTION.getEntry().getValue().run(program);

        long endMillis = System.currentTimeMillis();
        return Text.translatable("meazy:commands.run.info", (double) (endMillis - startMillis) / 1000);
    }
}
