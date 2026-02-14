package me.itzisonn_.meazy.instruction;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;

@NullMarked
public enum NumberType {
    INT(ConstantDescs.CD_int, false),
    LONG(ConstantDescs.CD_long, false),
    FLOAT(ConstantDescs.CD_float, false),
    DOUBLE(ConstantDescs.CD_double, false),

    BOXED_INT(ConstantDescs.CD_Integer, true),
    BOXED_LONG(ConstantDescs.CD_Long, true),
    BOXED_FLOAT(ConstantDescs.CD_Float, true),
    BOXED_DOUBLE(ConstantDescs.CD_Double, true);

    @Getter
    private final ClassDesc classDesc;
    @Getter
    private final boolean boxed;

    NumberType(ClassDesc classDesc, boolean boxed) {
        this.classDesc = classDesc;
        this.boxed = boxed;
    }

    public boolean isInt() {
        return this == INT || this == BOXED_INT;
    }

    public boolean isLong() {
        return this == LONG || this == BOXED_LONG;
    }

    public boolean isFloat() {
        return this == FLOAT || this == BOXED_FLOAT;
    }

    public boolean isDouble() {
        return this == DOUBLE || this == BOXED_DOUBLE;
    }



    public NumberType box() {
        return switch (this) {
            case INT -> BOXED_INT;
            case LONG -> BOXED_LONG;
            case FLOAT -> BOXED_FLOAT;
            case DOUBLE -> BOXED_DOUBLE;
            default -> this;
        };
    }

    public NumberType unbox() {
        return switch (this) {
            case BOXED_INT -> INT;
            case BOXED_LONG -> LONG;
            case BOXED_FLOAT -> FLOAT;
            case BOXED_DOUBLE -> DOUBLE;
            default -> this;
        };
    }



    @Nullable
    public static NumberType valueOf(ClassDesc classDesc) {
        for (NumberType numberType : values()) {
            if (numberType.getClassDesc().equals(classDesc)) return numberType;
        }

        return null;
    }

    public static boolean isNumberType(ClassDesc classDesc) {
        return valueOf(classDesc) != null;
    }

    public static NumberType getCommon(NumberType a, NumberType b) {
        if (a.isDouble() || b.isDouble()) return DOUBLE;
        if (a.isFloat() && b.isLong()) return DOUBLE;
        if (a.isLong() && b.isFloat()) return DOUBLE;

        if (a.isFloat() || b.isFloat()) return FLOAT;
        if (a.isInt() && b.isInt()) return INT;
        return LONG;
    }
}
