package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Represents statement that can have modifiers applied to it
 */
@Getter
@NullMarked
public abstract class ModifierStatement implements Statement {
    /**
     * ModifierStatement's modifiers
     */
    protected final Set<Modifier> modifiers;

    /**
     * ModifierStatement constructor
     * @param modifiers ModifierStatement's modifiers
     */
    public ModifierStatement(Set<Modifier> modifiers) {
        this.modifiers = modifiers;
    }
}
