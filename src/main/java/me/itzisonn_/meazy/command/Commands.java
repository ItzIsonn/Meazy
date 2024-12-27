package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.json_converters.Converters;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Commands {
    private static boolean isInit = false;

    private Commands() {}



    private static void register(String id, Command command) {
        Registries.COMMANDS.register(RegistryIdentifier.ofDefault(id), command);
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("Commands already initialized!");
        isInit = true;

        final Logger logger = MeazyMain.getInstance().getLogger();
        final String version = MeazyMain.getVersion();

        register("run", new Command(List.of("<file_to_run>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.getInstance().getLogger().log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                logger.log(Level.INFO, "Running file '{}'", file.getAbsoluteFile());

                String extension = Utils.getExtension(file);
                long startRunMillis = System.currentTimeMillis();
                if (extension.equals("mea")) {
                    ArrayList<Token> tokens = Registries.TOKENS_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));
                    Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(tokens);
                    Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().accept(program);
                }
                else if (extension.equals("meac")) {
                    Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
                    if (program == null) {
                        logger.log(Level.ERROR, "Failed to read file {}, try to run it in the same version of Meazy ({})", file.getAbsolutePath(), version);
                        return null;
                    }
                    if (Utils.isVersionAfter(version, program.getVersion())) {
                        logger.log(Level.ERROR, "Can't run file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), version);
                        return null;
                    }
                    if (!version.equals(program.getVersion())) {
                        logger.log(Level.WARN, "It's unsafe to run file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), version);
                    }
                    Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().accept(program);
                }
                else {
                    logger.log(Level.ERROR, "Can't run file with extension {}", extension);
                    return null;
                }
                long endRunMillis = System.currentTimeMillis();

                return "Executed in " + ((double) endRunMillis - (double) startRunMillis) / 1000 + "s.";
            }
        });

        register("compile", new Command(List.of("<file_to_compile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    logger.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                if (!Utils.getExtension(file).equals("mea")) {
                    logger.log(Level.ERROR, "Can't compile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                logger.log(Level.INFO, "Compiling file '{}'", file.getAbsoluteFile());

                long startCompileMillis = System.currentTimeMillis();
                ArrayList<Token> tokens = Registries.TOKENS_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));

                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(tokens);
                long endCompileMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    logger.log(Level.ERROR, "Output file can't be directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) logger.log(Level.INFO, "Created file '{}'", args[1]);
                            else logger.log(Level.INFO, "File '{}' already exists", args[1]);
                        }
                        catch (Exception e) {
                            logger.log(Level.ERROR, "Can't create file '{}'", args[1]);
                            return null;
                        }
                    }
                    else {
                        logger.log(Level.ERROR, "Can't create parent file");
                        return null;
                    }
                }

                String json = Converters.getGson().toJson(program, Program.class);

                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    fileWriter.write(json);
                    return "Compiled in " + ((double) endCompileMillis - (double) startCompileMillis) / 1000 + "s.";
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        register("decompile", new Command(List.of("<file_to_decompile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    logger.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                if (!Utils.getExtension(file).equals("meac")) {
                    logger.log(Level.ERROR, "Can't decompile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                logger.log(Level.INFO, "Decompiling file '{}'", file.getAbsoluteFile());

                long startDecompileMillis = System.currentTimeMillis();
                Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
                if (program == null) {
                    logger.log(Level.ERROR, "Failed to read file {}, try to decompile it in the same version of Meazy ({})", file.getAbsolutePath(), version);
                    return null;
                }
                if (Utils.isVersionAfter(version, program.getVersion())) {
                    logger.log(Level.ERROR, "Can't decompile file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), version);
                    return null;
                }
                if (!version.equals(program.getVersion())) {
                    logger.log(Level.WARN, "It's unsafe to decompile file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), version);
                }
                long endDecompileMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    logger.log(Level.ERROR, "Output file can't be directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) logger.log(Level.INFO, "Created file '{}'", args[1]);
                            else logger.log(Level.INFO, "File '{}' already exists", args[1]);
                        }
                        catch (Exception e) {
                            logger.log(Level.ERROR, "Can't create file '{}'", args[1]);
                            return null;
                        }
                    }
                    else {
                        logger.log(Level.ERROR, "Can't create parent file");
                        return null;
                    }
                }

                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    fileWriter.write(program.toCodeString(0));
                    return "Decompiled in " + ((double) endDecompileMillis - (double) startDecompileMillis) / 1000 + "s.";
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
