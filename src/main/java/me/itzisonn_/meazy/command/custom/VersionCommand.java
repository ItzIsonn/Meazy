package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import org.apache.logging.log4j.Level;

import java.util.List;

public class VersionCommand extends AbstractCommand {
    public VersionCommand() {
        super("version", List.of());
    }

    @Override
    public Text execute(String[] args) {
        MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.version", MeazyMain.VERSION));
        return null;
    }
}
