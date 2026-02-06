package me.itzisonn_.meazy.lang.text;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NullMarked
public class MergedText implements Text {
    private final List<Text> texts;

    MergedText(List<Text> texts) throws IllegalArgumentException {
        if (texts.isEmpty()) throw new IllegalArgumentException("Texts can't be empty");
        this.texts = new ArrayList<>(texts);
    }

    @Override
    public String toString() {
        return texts.stream().map(Text::toString).collect(Collectors.joining());
    }

    @Override
    public Text append(Text text) {
        if (text instanceof MergedText mergedText) texts.addAll(mergedText.texts);
        else texts.add(text);

        return this;
    }
}
