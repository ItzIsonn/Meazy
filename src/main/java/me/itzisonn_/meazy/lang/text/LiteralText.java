package me.itzisonn_.meazy.lang.text;

import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class LiteralText implements Text {
    private final String text;
    private final List<String> args;

    LiteralText(String text, List<String> args) {
        this.text = text;
        this.args = List.copyOf(args);
    }

    @Override
    public String toString() {
        String result = text;

        for (int i = 0; i < args.size(); i++) {
            result = result.replace("{" + i + "}", args.get(i));
        }

        return result;
    }
}
