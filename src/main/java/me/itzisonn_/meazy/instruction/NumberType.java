package me.itzisonn_.meazy.instruction;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;

@NullMarked
public enum NumberType {
    INT(ConstantDescs.CD_int),
    LONG(ConstantDescs.CD_long),
    FLOAT(ConstantDescs.CD_float),
    DOUBLE(ConstantDescs.CD_double);

    @Getter
    private final ClassDesc classDesc;

    NumberType(ClassDesc classDesc) {
        this.classDesc = classDesc;
    }

    @Nullable
    public static NumberType valueOf(ClassDesc classDesc) {
        for (NumberType numberType : values()) {
            if (numberType.classDesc.equals(classDesc)) return numberType;
        }

        return null;
    }

    public static boolean isNumberType(ClassDesc classDesc) {
        return valueOf(classDesc) != null;
    }

    public static NumberType getCommon(NumberType a, NumberType b) {
        if (a == DOUBLE || b == DOUBLE) return DOUBLE;
        if (a == FLOAT && b == LONG) return DOUBLE;
        if (a == LONG && b == FLOAT) return DOUBLE;

        if (a == FLOAT || b == FLOAT) return FLOAT;
        if (a == INT && b == INT) return INT;
        return LONG;
    }
}
