package me.itzisonn_.meazy.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
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
     * ClassDesc of this data type
     */
    private ClassDesc classDesc;
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

    public void resolve(Environment environment) {
        classDesc = EnvironmentUtils.resolveClassDesc(environment, classDesc, !isNullable);
    }



    public DataType with(ClassDesc classDesc) {
        return of(classDesc, isNullable);
    }

    public DataType with(boolean isNullable) {
        return of(classDesc, isNullable);
    }

    public DataType asNullable() {
        return ofNullable(classDesc);
    }

    public DataType asNonNull() {
        return ofNonNull(classDesc);
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



    public static DataType commonOf(Environment environment, DataType dataType1, DataType dataType2) {
        ClassDesc classDesc = EnvironmentUtils.getCommonOf(environment, dataType1.getClassDesc(), dataType2.getClassDesc());

        return of(
                classDesc == null ? ConstantDescs.CD_Object : classDesc,
                dataType1.isNullable() || dataType2.isNullable()
        );
    }

    public static boolean matches(Environment environment, DataType dataType, DataType target) {
        return EnvironmentUtils.isInstanceOf(environment, dataType.getClassDesc(), target.getClassDesc())
                && (!dataType.isNullable() || target.isNullable());
    }
}