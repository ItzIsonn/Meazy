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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CompileCommand extends AbstractCommand {
    public CompileCommand() {
        super("compile", List.of("<file_to_compile>", "<output_file_path>"));
    }

    @Override
    public Text execute(String[] args) {
        File file = new File(args[0]);
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.getAbsolutePath()));
            return null;
        }

        if (!FileUtils.getExtension(file).equals("mea")) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", FileUtils.getExtension(file)));
            return null;
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.compile.compiling'", file.getAbsolutePath()));

        long startMillis = System.currentTimeMillis();
        List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));

        Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);
        long endMillis = System.currentTimeMillis();


        File outputFile = new File(args[1]);
        if (file.isDirectory()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.output_cant_be_directory"));
            return null;
        }
        if (!outputFile.getParentFile().exists()) {
            if (outputFile.getParentFile().mkdirs()) {
                try {
                    if (outputFile.createNewFile()) MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:file.created", args[1]));
                    else MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:file.already_exists", args[1]));
                }
                catch (Exception e) {
                    MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.cant_create", args[1]));
                    return null;
                }
            }
            else {
                MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.cant_create_parent", args[1]));
                return null;
            }
        }

        String json = Registries.getGson().toJson(program, Program.class);
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(json);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Text.translatable("meazy:commands.compile.info", (double) (endMillis - startMillis) / 1000);
    }
}
