package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtilsKt;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;

/**
 * Defines which values can be stored in variables, args, etc.
 */
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
        classDesc = EnvironmentUtilsKt.resolveClassDesc(environment, classDesc, !isNullable);
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
        ClassDesc classDesc = EnvironmentUtilsKt.getCommonOf(environment, dataType1.getClassDesc(), dataType2.getClassDesc());

        return of(
                classDesc == null ? ConstantDescs.CD_Object : classDesc,
                dataType1.isNullable() || dataType2.isNullable()
        );
    }

    public static boolean matches(Environment environment, DataType dataType, DataType target) {
        return EnvironmentUtils.isInstanceOf(environment, dataType.getClassDesc(), target.getClassDesc())
                && (!dataType.isNullable() || target.isNullable());
    }

    public ClassDesc getClassDesc() {
        return this.classDesc;
    }

    public boolean isNullable() {
        return this.isNullable;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DataType)) return false;
        final DataType other = (DataType) o;
        final Object this$classDesc = this.getClassDesc();
        final Object other$classDesc = other.getClassDesc();
        if (this$classDesc == null ? other$classDesc != null : !this$classDesc.equals(other$classDesc)) return false;
        if (this.isNullable() != other.isNullable()) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $classDesc = this.getClassDesc();
        result = result * PRIME + ($classDesc == null ? 43 : $classDesc.hashCode());
        result = result * PRIME + (this.isNullable() ? 79 : 97);
        return result;
    }
}