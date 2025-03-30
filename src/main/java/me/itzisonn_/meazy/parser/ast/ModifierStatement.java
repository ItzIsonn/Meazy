package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;

import java.util.Set;

@Getter
public abstract class ModifierStatement implements Statement {
    protected final Set<Modifier> modifiers;

    public ModifierStatement(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public String toCodeString(int offset) throws IllegalArgumentException {
        StringBuilder modifiersBuilder = new StringBuilder();
        for (Modifier modifier : modifiers) {
            modifiersBuilder.append(modifier).append(" ");
        }

        return modifiersBuilder.toString();
    }
}
