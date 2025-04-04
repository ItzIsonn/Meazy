package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;

import java.util.Set;

/**
 * Represents statement that can have modifiers applied to it
 */
@Getter
public abstract class ModifierStatement implements Statement {
    /**
     * ModifierStatement's modifiers
     */
    protected final Set<Modifier> modifiers;

    /**
     * ModifierStatement constructor
     * @param modifiers ModifierStatement's modifiers
     * @throws NullPointerException If modifiers is null
     */
    public ModifierStatement(Set<Modifier> modifiers) throws NullPointerException {
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");
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
