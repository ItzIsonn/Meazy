package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;

public final class TokenTypeSets {
    private TokenTypeSets() {}



    public static TokenTypeSet KEYWORDS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("keywords")).getValue();
    }

    public static TokenTypeSet OPERATOR_ASSIGN() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("operator_assign")).getValue();
    }

    public static TokenTypeSet OPERATOR_POSTFIX() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("operator_postfix")).getValue();
    }

    public static TokenTypeSet MEMBER_ACCESS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("member_access")).getValue();
    }

    public static TokenTypeSet COMPARISON() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("comparison")).getValue();
    }

    public static TokenTypeSet MULTIPLICATION() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("multiplication")).getValue();
    }

    public static TokenTypeSet ADDITION() {
        return Registries.TOKEN_TYPE_SETS.getEntry(MeazyMain.getDefaultIdentifier("addition")).getValue();
    }
}
