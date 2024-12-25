package me.itzisonn_.meazy;

import lombok.Getter;
import me.itzisonn_.meazy.addons.AddonManager;
import me.itzisonn_.meazy.addons.Addon;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.json_converters.Converters;
import me.itzisonn_.meazy.registry.Registries;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public final class MeazyMain {
    @Getter
    private static final String version = "2.1";
    @Getter
    private static final MeazyMain instance = new MeazyMain();
    @Getter
    private final Logger logger = LogManager.getLogger("meazy");

    public static void main(String[] args) {
        instance.run(args);
    }

    private void run(String[] args) {
        if (args.length == 0) {
            logger.log(Level.ERROR, "Usage: [run <file_to_run> | compile <file_to_compile> <output_file_path> | decompile <file_to_decompile> <output_file_path>]");
            return;
        }

        File file = new File(args[1]);
        if (file.isDirectory() || !file.exists()) {
            logger.log(Level.ERROR, "File '{}' doesn't exist", file.getAbsoluteFile());
            return;
        }

        switch (args[0]) {
            case "run" -> {
                if (args.length != 2) {
                    logger.log(Level.ERROR, "Usage: run <file_to_run>");
                    return;
                }
                runFile(file);
            }
            case "compile" -> {
                if (args.length != 3) {
                    logger.log(Level.ERROR, "Usage: compile <file_to_compile> <output_file_path>");
                    return;
                }
                compileFile(file, args[2]);
            }
            case "decompile" -> {
                if (args.length != 3) {
                    logger.log(Level.ERROR, "Usage: decompile <file_to_compile> <output_file_path>");
                    return;
                }
                decompileFile(file, args[2]);
            }
            default -> logger.log(Level.ERROR, "Usage: [run <file_to_run> | compile <file_to_compile> <output_file_path> | decompile <file_to_decompile> <output_file_path>]");
        }
    }

    private void runFile(File file) {
        long startLoadMillis = System.currentTimeMillis();
        Registries.INIT();
        loadAddons();
        long endLoadMillis = System.currentTimeMillis();

        logger.log(Level.INFO, "Running file '{}'", file.getAbsoluteFile());

        String extension = Utils.getExtension(file);
        long startRunMillis = System.currentTimeMillis();
        if (extension.equals("mea")) {
            ArrayList<Token> tokens = Registries.TOKENS_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));
            Program program = Registries.PARSER.getEntry().getValue().produceAST(tokens);
            Registries.RUN_FUNCTION.getEntry().getValue().accept(program);
        }
        else if (extension.equals("meac")) {
            Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
            if (program == null) {
                logger.log(Level.ERROR, "Failed to read file {}, try to run it in the same version of Meazy ({})", file.getAbsolutePath(), version);
                return;
            }
            if (Utils.isVersionAfter(version, program.getVersion())) {
                logger.log(Level.ERROR, "Can't run file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), version);
                return;
            }
            if (!version.equals(program.getVersion())) {
                logger.log(Level.WARN, "It's unsafe to run file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), version);
                return;
            }
            Registries.RUN_FUNCTION.getEntry().getValue().accept(program);
        }
        else {
            logger.log(Level.ERROR, "Can't run file with extension {}", extension);
            return;
        }
        long endRunMillis = System.currentTimeMillis();

        System.out.println();
        logger.log(Level.INFO, "Loaded in {}s, executed in {}s",
                ((double) endLoadMillis - (double) startLoadMillis) / 1000,
                ((double) endRunMillis - (double) startRunMillis) / 1000);
    }

    private void compileFile(File file, String output) {
        if (!Utils.getExtension(file).equals("mea")) {
            logger.log(Level.ERROR, "Can't compile file with extension {}", Utils.getExtension(file));
            return;
        }

        long startLoadMillis = System.currentTimeMillis();
        Registries.INIT();
        loadAddons();
        long endLoadMillis = System.currentTimeMillis();

        logger.log(Level.INFO, "Compiling file '{}'", file.getAbsoluteFile());

        long startCompileMillis = System.currentTimeMillis();
        ArrayList<Token> tokens = Registries.TOKENS_FUNCTION.getEntry().getValue().apply(Utils.getLines(file));

        Program program = Registries.PARSER.getEntry().getValue().produceAST(tokens);
        long endCompileMillis = System.currentTimeMillis();


        File outputFile = new File(output);
        if (file.isDirectory()) {
            logger.log(Level.ERROR, "Output file can't be directory");
            return;
        }
        if (!outputFile.getParentFile().exists()) {
            if (outputFile.getParentFile().mkdirs()) {
                try {
                    if (outputFile.createNewFile()) logger.log(Level.INFO, "Created file '{}'", output);
                    else logger.log(Level.INFO, "File '{}' already exists", output);
                }
                catch (Exception e) {
                    logger.log(Level.ERROR, "Can't create file '{}'", output);
                    return;
                }
            }
            else {
                logger.log(Level.ERROR, "Can't create parent file");
                return;
            }
        }

        String json = Converters.getGson().toJson(program, Program.class);

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(json);
            logger.log(Level.INFO, "Loaded in {}s, compiled in {}s",
                    ((double) endLoadMillis - (double) startLoadMillis) / 1000,
                    ((double) endCompileMillis - (double) startCompileMillis) / 1000);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decompileFile(File file, String output) {
        if (!Utils.getExtension(file).equals("meac")) {
            logger.log(Level.ERROR, "Can't decompile file with extension {}", Utils.getExtension(file));
            return;
        }

        long startLoadMillis = System.currentTimeMillis();
        Registries.INIT();
        loadAddons();
        long endLoadMillis = System.currentTimeMillis();

        logger.log(Level.INFO, "Decompiling file '{}'", file.getAbsoluteFile());

        long startDecompileMillis = System.currentTimeMillis();
        Program program = Converters.getGson().fromJson(Utils.getLines(file), Program.class);
        if (program == null) {
            logger.log(Level.ERROR, "Failed to read file {}, try to decompile it in the same version of Meazy ({})", file.getAbsolutePath(), version);
            return;
        }
        if (Utils.isVersionAfter(version, program.getVersion())) {
            logger.log(Level.ERROR, "Can't decompile file that has been compiled by a more recent version of the Meazy ({}), in a more older version ({})", program.getVersion(), version);
            return;
        }
        if (!version.equals(program.getVersion())) {
            logger.log(Level.WARN, "It's unsafe to decompile file that has been compiled by a more older version of the Meazy ({}) in a more recent version ({})", program.getVersion(), version);
            return;
        }
        long endDecompileMillis = System.currentTimeMillis();


        File outputFile = new File(output);
        if (file.isDirectory()) {
            logger.log(Level.ERROR, "Output file can't be directory");
            return;
        }
        if (!outputFile.getParentFile().exists()) {
            if (outputFile.getParentFile().mkdirs()) {
                try {
                    if (outputFile.createNewFile()) logger.log(Level.INFO, "Created file '{}'", output);
                    else logger.log(Level.INFO, "File '{}' already exists", output);
                }
                catch (Exception e) {
                    logger.log(Level.ERROR, "Can't create file '{}'", output);
                    return;
                }
            }
            else {
                logger.log(Level.ERROR, "Can't create parent file");
                return;
            }
        }

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(program.toCodeString(0));
            logger.log(Level.INFO, "Loaded in {}s, decompiled in {}s",
                    ((double) endLoadMillis - (double) startLoadMillis) / 1000,
                    ((double) endDecompileMillis - (double) startDecompileMillis) / 1000);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void loadAddons() {
        logger.log(Level.INFO, "Loading addons...");

        File addonsDir;
        try {
            addonsDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "/addons/");
            if (!addonsDir.exists() && !addonsDir.mkdirs()) throw new RuntimeException("Can't load addons folder");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        AddonManager addonManager = new AddonManager();
        for (Addon addon : addonManager.loadAddons(addonsDir)) {
            addonManager.enableAddon(addon);
        }

        int addons = addonManager.getAddons().length;
        if (addons == 1) logger.log(Level.INFO, "1 addon loaded");
        else logger.log(Level.INFO, "{} addons loaded", addons);
    }
}