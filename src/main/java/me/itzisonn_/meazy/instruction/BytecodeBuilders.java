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
import java.util.*;
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
    private final Map<UUID, Optional<Label>> labels;

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

    public Label getLabel(UUID uuid) {
        return labels.getOrDefault(uuid, Optional.empty()).orElseThrow();
    }

    public boolean hasLabel(UUID uuid) {
        return labels.containsKey(uuid);
    }

    public boolean hasInitializedLabel(UUID uuid) {
        return labels.getOrDefault(uuid, Optional.empty()).isPresent();
    }

    public void setLabel(UUID uuid, Label label) {
        if (!labels.containsKey(uuid)) throw new IllegalArgumentException("Label with UUID " + uuid + " does not exist");
        labels.put(uuid, Optional.of(label));
    }

    public void addLabel(UUID uuid) {
        labels.put(uuid, Optional.empty());
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
