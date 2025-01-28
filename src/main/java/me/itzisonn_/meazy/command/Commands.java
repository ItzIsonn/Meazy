package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.json_converters.Converters;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * All basic Commands
 *
 * @see Registries#COMMANDS
 */
public final class Commands {
    private static boolean isInit = false;

    private Commands() {}



    private static void register(String id, Command command) {
        Registries.COMMANDS.register(RegistryIdentifier.ofDefault(id), command);
    }

    /**
     * Initializes {@link Registries#COMMANDS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#COMMANDS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Commands have already been initialized!");
        isInit = true;

        register("version", new Command(List.of()) {
            @Override
            public String execute(String[] args) {
                MeazyMain.LOGGER.log(Level.INFO, "Meazy version {}", MeazyMain.VERSION);
                return null;
            }
        });

        register("run", new Command(List.of("<file_to_run>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Running file '{}'", file.getAbsoluteFile());

                String extension = Utils.getExtension(file);
                long startMillis = System.currentTimeMillis();
                if (extension.equals("mea")) {
                    List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));
                    System.out.println(tokens);
                    Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(tokens);
                    Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().accept(program);
                }
                else if (extension.equals("meac")) {
                    Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
                    if (program == null) {
                        MeazyMain.LOGGER.log(Level.ERROR, "Failed to read file {}, try to run it in the same version of Meazy ({})", file.getAbsolutePath(), MeazyMain.VERSION);
                        return null;
                    }
                    if (Utils.isVersionAfter(MeazyMain.VERSION, program.getVersion())) {
                        MeazyMain.LOGGER.log(Level.ERROR, "Can't run file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), MeazyMain.VERSION);
                        return null;
                    }
                    if (!MeazyMain.VERSION.equals(program.getVersion())) {
                        MeazyMain.LOGGER.log(Level.WARN, "It's unsafe to run file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), MeazyMain.VERSION);
                    }
                    Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().accept(program);
                }
                else {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't run file with extension {}", extension);
                    return null;
                }
                long endMillis = System.currentTimeMillis();

                return "Executed in " + ((double) endMillis - (double) startMillis) / 1000 + "s.";
            }
        });

        register("compile", new Command(List.of("<file_to_compile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                if (!Utils.getExtension(file).equals("mea")) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't compile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Compiling file '{}'", file.getAbsoluteFile());

                long startMillis = System.currentTimeMillis();
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));

                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(tokens);
                long endMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Output file can't be directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) MeazyMain.LOGGER.log(Level.INFO, "Created file '{}'", args[1]);
                            else MeazyMain.LOGGER.log(Level.INFO, "File '{}' already exists", args[1]);
                        }
                        catch (Exception e) {
                            MeazyMain.LOGGER.log(Level.ERROR, "Can't create file '{}'", args[1]);
                            return null;
                        }
                    }
                    else {
                        MeazyMain.LOGGER.log(Level.ERROR, "Can't create parent file");
                        return null;
                    }
                }

                String json = Converters.getGson().toJson(program, Program.class);

                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    fileWriter.write(json);
                    return "Compiled in " + ((double) endMillis - (double) startMillis) / 1000 + "s.";
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
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
                    return null;
                }

                if (!Utils.getExtension(file).equals("meac")) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't decompile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Decompiling file '{}'", file.getAbsoluteFile());

                long startMillis = System.currentTimeMillis();
                Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
                if (program == null) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Failed to read file {}, try to decompile it in the same version of Meazy ({})", file.getAbsolutePath(), MeazyMain.VERSION);
                    return null;
                }
                if (Utils.isVersionAfter(MeazyMain.VERSION, program.getVersion())) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't decompile file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), MeazyMain.VERSION);
                    return null;
                }
                if (!MeazyMain.VERSION.equals(program.getVersion())) {
                    MeazyMain.LOGGER.log(Level.WARN, "It's unsafe to decompile file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), MeazyMain.VERSION);
                }
                long endMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Output file can't be directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) MeazyMain.LOGGER.log(Level.INFO, "Created file '{}'", args[1]);
                            else MeazyMain.LOGGER.log(Level.INFO, "File '{}' already exists", args[1]);
                        }
                        catch (Exception e) {
                            MeazyMain.LOGGER.log(Level.ERROR, "Can't create file '{}'", args[1]);
                            return null;
                        }
                    }
                    else {
                        MeazyMain.LOGGER.log(Level.ERROR, "Can't create parent file");
                        return null;
                    }
                }

                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    fileWriter.write(program.toCodeString(0));
                    return "Decompiled in " + ((double) endMillis - (double) startMillis) / 1000 + "s.";
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
