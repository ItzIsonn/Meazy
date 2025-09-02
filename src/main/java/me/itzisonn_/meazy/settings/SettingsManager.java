package me.itzisonn_.meazy.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.text.Text;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URISyntaxException;

public class SettingsManager {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Settings.class, new SettingsDeserializer()).create();
    @Getter
    private final Settings settings;

    public SettingsManager() {
        File settingsFile;
        try {
            settingsFile = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/settings.json");

            if (!settingsFile.exists()) {
                if (!settingsFile.createNewFile()) throw new RuntimeException(Text.translatable("meazy:settings.cant_load_file").getContent());
                saveDefaultSettings(settingsFile);
            }
        }
        catch (URISyntaxException | IOException e) {
            throw new RuntimeException(Text.translatable("meazy:settings.cant_load_file").getContent(), e);
        }

        Settings settings = gson.fromJson(FileUtils.getLines(settingsFile), Settings.class);
        if (settings == null) {
            MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:settings.invalid_file"));
            saveDefaultSettings(settingsFile);

            settings = gson.fromJson(FileUtils.getLines(settingsFile), Settings.class);
            if (settings == null) throw new RuntimeException(Text.translatable("meazy:settings.cant_create_default").getContent());
        }

        this.settings = settings;
    }

    private void saveDefaultSettings(File settingsFile) {
        InputStream in = MeazyMain.class.getClassLoader().getResourceAsStream("settings.json");
        if (in == null) throw new RuntimeException(Text.translatable("meazy:settings.cant_find_default").getContent());

        try {
            OutputStream out = new FileOutputStream(settingsFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        }
        catch (IOException e) {
            throw new RuntimeException(Text.translatable("meazy:settings.cant_create_default").getContent(), e);
        }
    }
}
