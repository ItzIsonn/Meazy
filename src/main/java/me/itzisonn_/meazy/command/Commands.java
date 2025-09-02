package me.itzisonn_.meazy.command;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
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
                MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:commands.version", MeazyMain.VERSION);
                return null;
            }
        });

        register(new Command("addons", List.of("[list | downloadDefault]")) {
            @Override
            public String execute(String... args) {
                switch (args[0]) {
                    case "list" -> {
                        if (MeazyMain.ADDON_MANAGER.getAddons().length == 0) {
                            MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.empty"));
                            return null;
                        }

                        MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.loaded"));
                        for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                            AddonInfo addonInfo = addon.getAddonInfo();

                            String authors;
                            if (!addonInfo.getAuthors().isEmpty()) {
                                authors = " " + Text.translatable("meazy:commands.addons.by") + " " + String.join(", ", addonInfo.getAuthors());
                            }
                            else authors = "";

                            String description;
                            if (!addonInfo.getDescription().isBlank()) {
                                description = " - " + addonInfo.getDescription();
                            }
                            else description = "";

                            MeazyMain.LOGGER.log(Level.INFO, Text.literal("    {}{}{}"),
                                    addonInfo.getFullName(),
                                    authors,
                                    description);
                        }
                        return null;
                    }

                    case "downloadDefault" -> {
                        String site = "https://github.com/ItzIsonn/MeazyAddon/releases/download/v" + MeazyMain.VERSION + "/MeazyAddon-v" + MeazyMain.VERSION + ".jar";
                        String file = MeazyMain.ADDON_MANAGER.getAddonsFolder().getAbsolutePath() + "\\" + Arrays.asList(site.split("/")).getLast();

                        ReadableByteChannel byteChannel;
                        try {
                            URL url = new URI(site).toURL();
                            byteChannel = Channels.newChannel(url.openStream());
                        }
                        catch (FileNotFoundException e) {
                            MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.addons.cant_find_default"), MeazyMain.VERSION);
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

                        MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.loaded_default"), MeazyMain.VERSION);
                        return null;
                    }

                    default -> {
                        MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.invalid_argument"), args[0]);
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
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.doesnt_exist", file.getAbsolutePath());
                    return null;
                }

                MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:commands.run.running", file.getAbsolutePath());

                String extension = FileUtils.getExtension(file);
                long startMillis = System.currentTimeMillis();

                Program program;
                switch (extension) {
                    case "mea" -> {
                        List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(FileUtils.getLines(file));
                        Parser.reset();
                        program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(file, tokens);
                    }
                    case "meac" -> {
                        program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
                        if (program == null) {
                            MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.failed_read", file.getAbsolutePath());
                            return null;
                        }
                        if (MeazyMain.VERSION.isBefore(program.getVersion())) {
                            MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:commands.run.incompatible_version", program.getVersion(), MeazyMain.VERSION);
                            return null;
                        }
                        if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                            MeazyMain.LOGGER.logTranslatable(Level.WARN, "meazy:commands.run.unsafe", program.getVersion(), MeazyMain.VERSION);
                        }
                        program.setFile(file);
                    }
                    default -> {
                        MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.unsupported_extension", extension);
                        return null;
                    }
                }

                Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().apply(program);
                long endMillis = System.currentTimeMillis();

                return "Executed in " + ((double) (endMillis - startMillis)) / 1000 + "s.";
            }
        });

        register(new Command("compile", List.of("<file_to_compile>", "<output_file_path>")) {
            @Override
            public String execute(String[] args) {
                File file = new File(args[0]);
                if (file.isDirectory() || !file.exists()) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.doesnt_exist", file.getAbsolutePath());
                    return null;
                }

                if (!FileUtils.getExtension(file).equals("mea")) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.unsupported_extension", FileUtils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:commands.compile.compiling'", file.getAbsolutePath());

                long startMillis = System.currentTimeMillis();
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(FileUtils.getLines(file));

                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(file, tokens);
                long endMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.output_cant_be_directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:file.created", args[1]);
                            else MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:file.already_exists", args[1]);
                        }
                        catch (Exception e) {
                            MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.cant_create", args[1]);
                            return null;
                        }
                    }
                    else {
                        MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.cant_create_parent", args[1]);
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
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.doesnt_exist", file.getAbsolutePath());
                    return null;
                }

                if (!FileUtils.getExtension(file).equals("meac")) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.unsupported_extension", FileUtils.getExtension(file));
                    return null;
                }

                MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:commands.decompile.decompiling", file.getAbsolutePath());

                long startMillis = System.currentTimeMillis();
                Program program = Registries.getGson().fromJson(FileUtils.getLines(file), Program.class);
                if (program == null) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.failed_read", file.getAbsolutePath());
                    return null;
                }
                if (MeazyMain.VERSION.isBefore(program.getVersion())) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:commands.decompile.incompatible_version", program.getVersion(), MeazyMain.VERSION);
                    return null;
                }
                if (MeazyMain.VERSION.isAfter(program.getVersion())) {
                    MeazyMain.LOGGER.logTranslatable(Level.WARN, "meazy:commands.decompile.unsafe", program.getVersion(), MeazyMain.VERSION);
                }
                program.setFile(file);
                long endMillis = System.currentTimeMillis();


                File outputFile = new File(args[1]);
                if (file.isDirectory()) {
                    MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.output_cant_be_directory");
                    return null;
                }
                if (!outputFile.getParentFile().exists()) {
                    if (outputFile.getParentFile().mkdirs()) {
                        try {
                            if (outputFile.createNewFile()) MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:file.created", args[1]);
                            else MeazyMain.LOGGER.logTranslatable(Level.INFO, "meazy:file.already_exists", args[1]);
                        }
                        catch (Exception e) {
                            MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.cant_create", args[1]);
                            return null;
                        }
                    }
                    else {
                        MeazyMain.LOGGER.logTranslatable(Level.ERROR, "meazy:file.cant_create_parent");
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
