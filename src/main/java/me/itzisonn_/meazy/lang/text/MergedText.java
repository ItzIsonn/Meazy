package me.itzisonn_.meazy.lang.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents several {@link Text}s merged together
 */
public class MergedText implements Text {
    private final List<Text> texts;

    /**
     * @param texts List of texts
     * @throws NullPointerException If given texts is null
     * @throws IllegalArgumentException If given texts is empty or contains null members
     */
    public MergedText(List<Text> texts) throws NullPointerException, IllegalArgumentException {
        if (texts == null) throw new NullPointerException("Texts can't be null");
        if (texts.isEmpty()) throw new IllegalArgumentException("Texts can't be empty");
        if (texts.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Texts can't contain null members");

        this.texts = new ArrayList<>(texts);
    }

    @Override
    public String toString() {
        return texts.stream().map(Text::toString).collect(Collectors.joining());
    }

    @Override
    public Text append(Text text) {
        if (text == null) throw new NullPointerException("Text can't be null");

        if (text instanceof MergedText mergedText) texts.addAll(mergedText.texts);
        else texts.add(text);

        return this;
    }
}
