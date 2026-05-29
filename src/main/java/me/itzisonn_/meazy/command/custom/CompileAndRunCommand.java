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
import java.io.IOException;
import java.lang.constant.ClassDesc;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@NullMarked
public class CompileAndRunCommand extends AbstractCommand {
    public CompileAndRunCommand() {
        super("compile_and_run", List.of("<target_file>", "<output_directory_path>"));
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

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.compile.compiling", file.getAbsolutePath()));
        long startCompileMillis = System.currentTimeMillis();

        List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(FileUtils.getLines(file));
        Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens);
        Map<ClassDesc, byte[]> classes = Registries.COMPILE_PROGRAM_FUNCTION.getEntry().getValue().compile(program);

        File outputDirectory = new File(args[1]);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new RuntimeException("Failed to create output directory"); //TODO
            }
        }
        else Arrays.stream(outputDirectory.listFiles()).forEach(File::delete);

        for (ClassDesc classDesc : classes.keySet()) {
            byte[] classFile = classes.get(classDesc);
            File outputFile = new File(outputDirectory, classDesc.displayName() + ".class");

            try {
                Files.write(outputFile.toPath(), classFile);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        long endCompileMillis = System.currentTimeMillis();
        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.compile.info", (double) (endCompileMillis - startCompileMillis) / 1000));

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.run.running", file.getAbsolutePath()));
        long startRunMillis = System.currentTimeMillis();
        Registries.RUN_PROGRAM_FUNCTION.getEntry().getValue().run(classes);

        long endRunMillis = System.currentTimeMillis();
        return Text.translatable("meazy:commands.run.info", (double) (endRunMillis - startRunMillis) / 1000);
    }
}
