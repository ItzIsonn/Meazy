package me.itzisonn_.meazy.instruction;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@NullMarked
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class BytecodeBuilders {
    @Nullable
    private final ClassBuilder classBuilder;
    @Nullable
    private final CodeBuilder codeBuilder;
    private final LinkedHashMap<ClassDesc, byte[]> classes;
    private final Map<UUID, Label> labels;

    public static BytecodeBuilders of(@Nullable ClassBuilder classBuilder, @Nullable CodeBuilder codeBuilder) {
        return new BytecodeBuilders(classBuilder, codeBuilder, new LinkedHashMap<>(), new HashMap<>());
    }

    public void withClass(ClassDesc classDesc, Consumer<ClassBuilder> classBuilder) {
        byte[] classFile = ClassFile.of().build(
                classDesc,
                classBuilder
        );

        classes.put(classDesc, classFile);
    }

    public Map<ClassDesc, byte[]> getClasses() {
        return new LinkedHashMap<>(classes);
    }

    @Nullable
    public Label getLabel(UUID uuid) {
        return labels.get(uuid);
    }

    public void setLabel(UUID uuid, Label label) {
        labels.put(uuid, label);
    }



    public BytecodeBuilders copy(@Nullable ClassBuilder classBuilder, @Nullable CodeBuilder codeBuilder) {
        return new BytecodeBuilders(classBuilder, codeBuilder, classes, labels);
    }

    public BytecodeBuilders copy(@Nullable ClassBuilder classBuilder) {
        return new BytecodeBuilders(classBuilder, codeBuilder, classes, labels);
    }

    public BytecodeBuilders copy(@Nullable CodeBuilder codeBuilder) {
        return new BytecodeBuilders(classBuilder, codeBuilder, classes, labels);
    }
}
