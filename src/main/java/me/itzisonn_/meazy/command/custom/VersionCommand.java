package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.logging.LogLevel;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class VersionCommand extends AbstractCommand {
    public VersionCommand() {
        super("version", List.of());
    }

    @Override
    @Nullable
    public Text execute(String[] args) {
        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.version", MeazyMain.VERSION));
        return null;
    }
}
