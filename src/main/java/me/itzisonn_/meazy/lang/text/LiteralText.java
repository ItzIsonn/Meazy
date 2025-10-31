package me.itzisonn_.meazy.lang.text;

import java.util.List;

/**
 * Represents text that is the same across multiple languages
 */
public class LiteralText implements Text {
    private final String text;
    private final List<String> args;

    /**
     * @param text Text
     * @throws NullPointerException If given text is null
     */
    protected LiteralText(String text, List<String> args) throws NullPointerException {
        if (text == null) throw new NullPointerException("Text can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        this.text = text;
        this.args = List.copyOf(args);
    }

    @Override
    public String toString() {
        String result = text;

        for (int i = 0; i < args.size(); i++) {
            result = result.replace("{" + i + "}", String.valueOf(args.get(i)));
        }

        return result;
    }
}
