package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

/**
 * All basic Commands
 *
 * @see Registries#COMMANDS
 */
public final class Commands {
    private static boolean isInit = false;

    private Commands() {}



    public static Command VERSION() {
        return Registries.COMMANDS.getEntry(Registries.getDefaultIdentifier("version")).getValue();
    }

    public static Command DOWNLOAD_DEFAULT_ADDON() {
        return Registries.COMMANDS.getEntry(Registries.getDefaultIdentifier("download_default_addon")).getValue();
    }

    public static Command RUN() {
        return Registries.COMMANDS.getEntry(Registries.getDefaultIdentifier("run")).getValue();
    }

    public static Command COMPILE() {
        return Registries.COMMANDS.getEntry(Registries.getDefaultIdentifier("compile")).getValue();
    }

    public static Command DECOMPILE() {
        return Registries.COMMANDS.getEntry(Registries.getDefaultIdentifier("decompile")).getValue();
    }



    /**
     * Finds registered Command with given name
     *
     * @param name Command's name
     * @return Command with given name or null
     */
    public static Command getByName(String name) {
        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            if (entry.getValue().getName().equals(name)) return entry.getValue();
        }

        return null;
    }



    private static void register(String id, Command command) {
        Registries.COMMANDS.register(Registries.getDefaultIdentifier(id), command);
    }

    private static void register(Command command) {
        register(command.getName(), command);
    }

    /**
     * Initializes {@link Registries#COMMANDS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#COMMANDS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Commands have already been initialized");
        isInit = true;

        register(new Command("version", List.of()) {
            @Override
            public String execute(String[] args) {
                MeazyMain.LOGGER.log(Level.INFO, "Meazy version {}", MeazyMain.VERSION);
                return null;
            }
        });

        register(new Command("addons", List.of("[list | downloadDefault]")) {
            @Override
            public String execute(String... args) {
                switch (args[0]) {
                    case "list" -> {
                        if (MeazyMain.ADDON_MANAGER.getAddons().length == 0) {
                            MeazyMain.LOGGER.log(Level.INFO, "No addons present");
                            return null;
                        }

                        MeazyMain.LOGGER.log(Level.INFO, "Loaded addons:");
                        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                            AddonInfo addonInfo = addon.getAddonInfo();

                            String authors;
                            if (!addonInfo.getAuthors().isEmpty()) {
                                authors = " by " + String.join(", ", addonInfo.getAuthors());
                            }
                            else authors = "";

                            String description;
                            if (!addonInfo.getDescription().isBlank()) {
                                description = " - " + addonInfo.getDescription();
                            }
                            else description = "";

                            MeazyMain.LOGGER.log(Level.INFO, "    {}{}{}",
                                    addonInfo.getFullName(),
                                    authors,
                                    description);
                        }
                        return null;
                    }

                    case "downloadDefault" -> {
                        String site = "https://github.com/ItzIsonn/MeazyAddon/releases/download/v" + MeazyMain.VERSION + "/MeazyAddon-v" + MeazyMain.VERSION + ".jar";
                        String file = MeazyMain.ADDONS_DIRECTORY.getAbsolutePath() + "\\" + Arrays.asList(site.split("/")).getLast();

                        ReadableByteChannel byteChannel;
                        try {
                            URL url = new URI(site).toURL();
                            byteChannel = Channels.newChannel(url.openStream());
                        }
                        catch (FileNotFoundException e) {
                            MeazyMain.LOGGER.log(Level.ERROR, "Can't find MeazyAddon for {} Meazy version. It'll probably release soon", MeazyMain.VERSION);
                            return null;
                        }
                        catch (URISyntaxException | IOException e) {
                            throw new RuntimeException(e);
                        }

                        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                            fileOutputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        MeazyMain.LOGGER.log(Level.INFO, "Successfully loaded MeazyAddon v{}", MeazyMain.VERSION);
                        return null;
                    }

                    default -> {
                        MeazyMain.LOGGER.log(Level.ERROR, "Unknown argument {}", args[0]);
                        return null;
                    }
                }
            }
        });

        register(new Command("run", List.of("<file_to_run>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsolutePath());
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Running file '{}'", file.getAbsolutePath());

                String extension = Utils.getExtension(file);
                long startMillis = System.currentTimeMillis();

                Program program;
                switch (extension) {
                    case "mea" -> {
                        List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));
                        program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(tokens);
                    }
                    case "meac" -> {
                        program = Registries.getGson().fromJson(Utils.getLines(file), Program.class);
                        if (program == null) {
                            MeazyMain.LOGGER.log(Level.ERROR, "Failed to read file {}", file.getAbsolutePath());
                            return null;
                        }
                        if (MeazyMain.VERSION.isBefore(program.getVersion())) {
                            MeazyMain.LOGGER.log(Level.ERROR, "Can't run file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), MeazyMain.VERSION);
                            return null;
                        }
                        if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                            MeazyMain.LOGGER.log(Level.WARN, "It's unsafe to run file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), MeazyMain.VERSION);
                        }
                    }
                    default -> {
                        MeazyMain.LOGGER.log(Level.ERROR, "Can't run file with extension {}", extension);
                        return null;
                    }
                }

                Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().apply(program, file);
                long endMillis = System.currentTimeMillis();

                return "Executed in " + ((double) (endMillis - startMillis)) / 1000 + "s.";
            }
        });

        register(new Command("compile", List.of("<file_to_compile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsolutePath());
                    return null;
                }

                if (!Utils.getExtension(file).equals("mea")) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't compile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Compiling file '{}'", file.getAbsolutePath());

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

                String json = Registries.getGson().toJson(program, Program.class);

                try (FileWriter fileWriter = new FileWriter(outputFile)) {
                    fileWriter.write(json);
                    return "Compiled in " + ((double) (endMillis - startMillis)) / 1000 + "s.";
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        register(new Command("decompile", List.of("<file_to_decompile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsolutePath());
                    return null;
                }

                if (!Utils.getExtension(file).equals("meac")) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't decompile file with extension {}", Utils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, "Decompiling file '{}'", file.getAbsolutePath());

                long startMillis = System.currentTimeMillis();
                Program program = Registries.getGson().fromJson(Utils.getLines(file), Program.class);
                if (program == null) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Failed to read file {}", file.getAbsolutePath());
                    return null;
                }
                if (MeazyMain.VERSION.isBefore(program.getVersion())) {
                    MeazyMain.LOGGER.log(Level.ERROR, "Can't decompile file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), MeazyMain.VERSION);
                    return null;
                }
                if (MeazyMain.VERSION.isAfter(program.getVersion())) {
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
                    return "Decompiled in " + ((double) (endMillis - startMillis)) / 1000 + "s.";
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
