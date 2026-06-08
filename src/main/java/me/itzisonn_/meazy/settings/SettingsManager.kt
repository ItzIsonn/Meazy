package me.itzisonn_.meazy.settings

import com.google.gson.GsonBuilder
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.util.FileUtils.getLines
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class SettingsManager {
    val settings: Settings

    init {
        val settingsFile: File
        try {
            settingsFile = File(
                File(
                    MeazyMain::class.java.protectionDomain.codeSource.location.toURI().getPath()
                ).getParent() + "/settings.json"
            )

            if (!settingsFile.exists()) {
                if (!settingsFile.createNewFile()) throw RuntimeException(
                    Text.translatable("meazy:settings.cant_load_file").toString()
                )
                saveDefaultSettings(settingsFile)
            }
        }
        catch (e: URISyntaxException) {
            throw RuntimeException(Text.translatable("meazy:settings.cant_load_file").toString(), e)
        }
        catch (e: IOException) {
            throw RuntimeException(Text.translatable("meazy:settings.cant_load_file").toString(), e)
        }

        settings = gson.fromJson(getLines(settingsFile), Settings::class.java)
    }

    private fun saveDefaultSettings(settingsFile: File) {
        val stream = MeazyMain::class.java.classLoader.getResourceAsStream("settings.json")
            ?: throw RuntimeException(Text.translatable("meazy:settings.cant_find_file").toString())

        try {
            Files.copy(stream, settingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        catch (e: IOException) {
            throw RuntimeException(Text.translatable("meazy:settings.cant_create_file").toString(), e)
        }
    }

    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(Settings::class.java, SettingsDeserializer)
            .create()
    }
}

class Settings(val language: String = "en", val exceptionAbsentKey: Boolean)