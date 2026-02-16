package me.itzisonn_.meazy.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.util.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@NullMarked
public class SettingsManager {
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Settings.class, new SettingsDeserializer()).create();
    @Getter
    private final Settings settings;

    public SettingsManager() {
        File settingsFile;
        try {
            settingsFile = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/settings.json");

            if (!settingsFile.exists()) {
                if (!settingsFile.createNewFile()) throw new RuntimeException(Text.translatable("meazy:settings.cant_load_file").toString());
                saveDefaultSettings(settingsFile);
            }
        }
        catch (URISyntaxException | IOException e) {
            throw new RuntimeException(Text.translatable("meazy:settings.cant_load_file").toString(), e);
        }

        settings = gson.fromJson(FileUtils.getLines(settingsFile), Settings.class);
    }

    private void saveDefaultSettings(File settingsFile) {
        InputStream in = MeazyMain.class.getClassLoader().getResourceAsStream("settings.json");
        if (in == null) throw new RuntimeException(Text.translatable("meazy:settings.cant_find_file").toString());

        try {
            Files.copy(in, settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException e) {
            throw new RuntimeException(Text.translatable("meazy:settings.cant_create_file").toString(), e);
        }
    }
}
