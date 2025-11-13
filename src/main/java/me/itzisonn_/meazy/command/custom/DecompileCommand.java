package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.logging.LogLevel;
import me.itzisonn_.meazy.parser.ast.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DecompileCommand extends AbstractCommand {
    public DecompileCommand() {
        super("decompile", List.of("<file_to_decompile>", "<output_file_path>"));
    }

    @Override
    public Text execute(String[] args) {
        File file = new File(args[0]);
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.getAbsolutePath()));
            return null;
        }

        if (!FileUtils.getExtension(file).equals("meac")) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", FileUtils.getExtension(file)));
            return null;
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.decompile.decompiling", file.getAbsolutePath()));

        long startMillis = System.currentTimeMillis();
        Program program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
        if (program == null) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.failed_read", file.getAbsolutePath()));
            return null;
        }
        if (MeazyMain.VERSION.isBefore(program.getVersion())) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:commands.decompile.incompatible_version", program.getVersion(), MeazyMain.VERSION));
            return null;
        }
        if (MeazyMain.VERSION.isAfter(program.getVersion())) {
            MeazyMain.LOGGER.log(LogLevel.WARNING, Text.translatable("meazy:commands.decompile.unsafe", program.getVersion(), MeazyMain.VERSION));
        }
        program.setFile(file);
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
                MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.cant_create_parent"));
                return null;
            }
        }

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(program.toCodeString(0));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Text.translatable("meazy:commands.decompile.info", (double) (endMillis - startMillis) / 1000);
    }
}
