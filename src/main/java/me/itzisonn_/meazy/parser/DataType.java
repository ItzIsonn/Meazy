package me.itzisonn_.meazy.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;

/**
 * Defines which values can be stored in variables, args, etc.
 */
@Getter
@EqualsAndHashCode
@NullMarked
public final class DataType {
    /**
     * TODO
     */
    private final ClassDesc classDesc;
    /**
     * Whether this data type accepts null values
     */
    private final boolean isNullable;

    private DataType(ClassDesc classDesc, boolean isNullable) {
        this.classDesc = classDesc;
        this.isNullable = isNullable;
    }

    @Override
    public String toString() {
        return classDesc.displayName() + (isNullable ? "?" : "");
    }



    public static DataType of(ClassDesc classDesc, boolean isNullable) {
        return new DataType(classDesc, isNullable);
    }

    public static DataType ofNullable(ClassDesc classDesc) {
        return of(classDesc, true);
    }

    public static DataType ofNonNull(ClassDesc classDesc) {
        return of(classDesc, false);
    }

    public static DataType any(boolean isNullable) {
        return of(ConstantDescs.CD_Object, isNullable);
    }

    public static DataType anyNullable() {
        return any(true);
    }

    public static DataType anyNonNull() {
        return any(false);
    }
}