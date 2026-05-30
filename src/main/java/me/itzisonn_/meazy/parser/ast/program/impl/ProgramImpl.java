package me.itzisonn_.meazy.parser.ast.program.impl;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.statement.DeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement;
import me.itzisonn_.meazy.util.FileUtils;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.program.Program;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy.util.MiscUtils;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@NullMarked
public class ProgramImpl implements Program {
    @Nullable
    private File file;
    private final Version version;
    private final Map<String, @Nullable Version> requiredAddons;
    private final List<Statement> body;
    @Nullable
    private FileEnvironment fileEnvironment;

    ProgramImpl(@Nullable File file, Version version, Map<String, @Nullable Version> requiredAddons, List<Statement> body) throws IllegalArgumentException {
        if (file != null) {
            if (!file.exists()) throw new IllegalArgumentException("File doesn't exist");
            if (file.isDirectory()) throw new IllegalArgumentException("File can't be directory");
        }

        this.file = file;
        this.version = version;
        this.requiredAddons = requiredAddons;
        this.body = body;
    }

    @Override
    public void setFile(File file) throws IllegalStateException, IllegalArgumentException {
        if (this.file != null) throw new IllegalStateException("Can't override existing file value");

        if (!file.exists()) throw new IllegalArgumentException("File doesn't exist");
        if (file.isDirectory()) throw new IllegalArgumentException("File can't be directory");

        this.file = file;
    }

    @Override
    public void declare(Environment environment) {
        if (!(environment instanceof GlobalEnvironment globalEnvironment)) throw new RuntimeException("Environment must be global");
        if (file == null) throw new IllegalArgumentException("File can't be null"); //TODO make file actually nonnull
        List<String> path = List.of(file.getAbsolutePath().split("\\\\"));

        String id = FileUtils.getNameWithoutExtension(file);
        id = id.substring(0, 1).toUpperCase() + id.substring(1);

        fileEnvironment = Registries.FILE_ENVIRONMENT_FACTORY.getEntry().getValue().create(globalEnvironment, path.get(path.size() - 2), id);
        globalEnvironment.addFileEnvironment(fileEnvironment);

        for (Statement statement : body) {
            if (statement instanceof ImportStatement importStatement) {
                fileEnvironment.addImport(importStatement.getName());
            }
        }

        for (Statement statement : body) {
            if (statement instanceof DeclarationStatement declarationStatement) {
                declarationStatement.declare(fileEnvironment);
            }
        }
    }

    @Override
    public void resolve(Environment environment) {
        if (fileEnvironment == null) throw new IllegalArgumentException("Program must be resolved TODO");

        for (Statement statement : body) {
            if (statement instanceof DeclarationStatement declarationStatement) {
                declarationStatement.resolve(fileEnvironment);
            }
        }
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, @Nullable ProgramUnit parent) {
        if (fileEnvironment == null) throw new IllegalArgumentException("Program must be resolved TODO");
        if (file == null) throw new IllegalArgumentException("File can't be null"); //TODO make file actually nonnull

        List<String> path = List.of(file.getAbsolutePath().split("\\\\"));

        String id = FileUtils.getNameWithoutExtension(file);
        id = id.substring(0, 1).toUpperCase() + id.substring(1);

        FileEnvironment fileEnvironment = this.fileEnvironment;
        ClassDesc classDesc = ClassDesc.of(path.get(path.size() - 2), id);

        List<InnerClassesAttribute> attributes = new ArrayList<>();
        for (Statement statement : body) {
            if (!(statement instanceof ClassDeclarationStatement classDeclarationStatement)) continue;
            if (!classDeclarationStatement.getModifiers().contains(Modifiers.PRIVATE())) continue;
            attributes.add(classDeclarationStatement.getInnerClassesAttribute(fileEnvironment));
        }

        instructionsSet.withClass(
                classDesc,
                List.of(AccessFlag.PUBLIC, AccessFlag.FINAL),
                attributes,
                classInstructions -> {
                    for (Statement statement : body) {
                        statement.emit(classInstructions, fileEnvironment, this);
                    }

                    classInstructions.withConstructor(
                            MethodTypeDesc.of(ConstantDescs.CD_void),
                            AccessFlag.PRIVATE.mask(),
                            bodyInstructions -> {
                                bodyInstructions.loadThisReference();

                                bodyInstructions.invokeSuperClass(
                                        ConstantDescs.CD_Object,
                                        MethodTypeDesc.of(ConstantDescs.CD_void),
                                        _ -> {}
                                );

                                bodyInstructions.returnVoid();
                            }
                    );

                    classInstructions.withConstructor(
                            MethodTypeDesc.of(ConstantDescs.CD_void),
                            AccessFlag.STATIC.mask(),
                            bodyInstructions -> {
                                for (VariableValue variableValue : fileEnvironment.getVariables()) {
                                    Expression value = variableValue.getInitializer();
                                    if (value == null) continue;

                                    value.emit(bodyInstructions, fileEnvironment, this);
                                    ClassDesc valueType = value.getType(fileEnvironment, this).getClassDesc();
                                    ClassDesc variableType = variableValue.getDataType().getClassDesc();

                                    if (!valueType.equals(variableType)) {
                                        if (NumberType.isNumberType(variableType) && NumberType.isNumberType(valueType)) {
                                            bodyInstructions.convertToNumberType(valueType, variableType);
                                        }

                                        else if (MiscUtils.isBoolean(variableType) && MiscUtils.isBoolean(valueType)) {
                                            bodyInstructions.convertToBooleanType(valueType.isClassOrInterface(), variableType.isClassOrInterface());
                                        }
                                    }

                                    bodyInstructions.storeStaticField(
                                            classDesc,
                                            variableValue.getId(),
                                            variableType
                                    );
                                }

                                bodyInstructions.returnVoid();
                            }
                    );
                }
        );
    }
}