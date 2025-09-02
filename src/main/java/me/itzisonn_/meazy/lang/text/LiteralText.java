package me.itzisonn_.meazy.lang.text;

/**
 * Represents text that is the same across multiple languages
 */
public class LiteralText implements Text {
    private final String text;

    /**
     * @param text Text
     * @throws NullPointerException If given text is null
     */
    protected LiteralText(String text) throws NullPointerException {
        if (text == null) throw new NullPointerException("Text can't be null");
        this.text = text;
    }

    @Override
    public String getContent() {
        return text;
    }

    @Override
    public String toString() {
        return "LiteralText(text=" + text + ")";
    }
}
