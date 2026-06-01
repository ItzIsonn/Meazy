package me.itzisonn_.meazy.util;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

@NullMarked
public final class MiscUtils {
    private MiscUtils() {}


    /**
     * Generates name with prefix:<br>
     * - If given name is uppercase, returns value in format PREFIX_NAME<br>
     * - Else returns value in format prefixName
     *
     * @param prefix Prefix
     * @param name Name
     * @return Generated name
     */
    public static String generatePrefixedName(String prefix, String name) {
        if (name.equals(name.toUpperCase())) return prefix.toUpperCase() + "_" + name;
        return prefix + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static boolean isBoolean(ClassDesc classDesc) {
        return classDesc.equals(ConstantDescs.CD_boolean) || classDesc.equals(ConstantDescs.CD_Boolean);
    }

    public static ClassDesc getBoxedType(ClassDesc classDesc) {
        if (!classDesc.isPrimitive()) return classDesc;

        return switch (classDesc.descriptorString()) {
            case "I" -> ConstantDescs.CD_Integer;
            case "J" -> ConstantDescs.CD_Long;
            case "F" -> ConstantDescs.CD_Float;
            case "D" -> ConstantDescs.CD_Double;
            case "Z" -> ConstantDescs.CD_Boolean;
            default -> classDesc;
        };
    }

    public static void boxPrimitive(InstructionsSet instructionsSet, ClassDesc classDesc) {
        ClassDesc boxedClassDesc = getBoxedType(classDesc);
        if (boxedClassDesc.equals(ConstantDescs.CD_void)) return;

        instructionsSet.invokeMethod(
                boxedClassDesc,
                "valueOf",
                MethodTypeDesc.of(boxedClassDesc, classDesc),
                _ -> {},
                InvokeType.STATIC
        );
    }

    public static boolean convertPrimitiveOrBoxed(InstructionsSet instructionsSet, ClassDesc from, ClassDesc to) {
        NumberType fromNumberType = NumberType.valueOf(from);
        NumberType toNumberType = NumberType.valueOf(to);

        if (fromNumberType != null && toNumberType != null) {
            instructionsSet.convertToNumberType(fromNumberType, toNumberType);
            return true;
        }
        else if (isBoolean(from) && isBoolean(to)) {
            instructionsSet.convertToBooleanType(from.isClassOrInterface(), to.isClassOrInterface());
            return true;
        }

        return false;
    }
}
