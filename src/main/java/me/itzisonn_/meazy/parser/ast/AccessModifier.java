package me.itzisonn_.meazy.parser.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class AccessModifier {
    private final String id;

    public AccessModifier(String id) {
        this.id = id;
    }
}