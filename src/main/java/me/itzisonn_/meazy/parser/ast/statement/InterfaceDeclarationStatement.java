package me.itzisonn_.meazy.parser.ast.statement;

import kotlin.Unit;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.attribute.InnerClassInfo;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.ClassDesc;
import java.lang.reflect.AccessFlag;
import java.util.*;

@Getter
@NullMarked
public class InterfaceDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final String id;
    private final Set<String> baseClasses;
    private final List<Statement> body;
    @Nullable
    private ClassEnvironment classEnvironment;

    public InterfaceDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body) {
        super(modifiers);
        this.id = id;
        this.baseClasses = baseClasses;
        this.body = body;
    }

    @Override
    public void declare(Environment environment) {
        if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
            throw new IllegalArgumentException("Environment must be file TODO");
        }

        boolean isInner = getModifiers().contains(Modifiers.PRIVATE());

        ClassEnvironment classEnvironment = ClassEnvironmentKt.ClassEnvironment(
                classDeclarationEnvironment,
                isInner || modifiers.contains(Modifiers.SHARED()),
                true,
                id,
                baseClasses,
                modifiers
        );

        classDeclarationEnvironment.declareClass(classEnvironment);
        this.classEnvironment = classEnvironment;

        for (Statement statement : body) {
            if (statement instanceof DeclarationStatement declarationStatement) {
                declarationStatement.declare(classEnvironment);
            }
        }
    }

    @Override
    public void resolve(Environment environment) {
        if (classEnvironment == null) {
            throw new RuntimeException("Class isn't declared TODO");
        }

        classEnvironment.resolveBaseClasses();

        for (Statement statement : body) {
            if (statement instanceof DeclarationStatement declarationStatement) {
                declarationStatement.resolve(classEnvironment);
            }
        }
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        if (!(environment instanceof FileEnvironment fileEnvironment)) throw new IllegalArgumentException("Environment must be file TODO");
        if (classEnvironment == null) throw new RuntimeException("Declared class is unresolved TODO");

        boolean isInner = getModifiers().contains(Modifiers.PRIVATE());

        ClassDesc classDesc;
        if (isInner) classDesc = ClassDesc.of(fileEnvironment.getPackageName() + "." + fileEnvironment.getClassName() + "$" + id);
        else classDesc = ClassDesc.of(fileEnvironment.getPackageName(), id);

        List<InnerClassesAttribute> attributes = new ArrayList<>();
        Set<AccessFlag> flags = new HashSet<>();
        flags.add(AccessFlag.INTERFACE);
        flags.add(AccessFlag.ABSTRACT);

        if (isInner) attributes.add(getInnerClassesAttribute(fileEnvironment));
        else {
            if (modifiers.contains(Modifiers.PRIVATE())) flags.add(AccessFlag.PRIVATE);
            else flags.add(AccessFlag.PUBLIC);
        }

        instructionsSet.withClass(
                classDesc,
                null,
                classEnvironment.getInterfaces(),
                flags,
                attributes,
                classInstructions -> {
                    for (Statement statement : body) {
                        statement.emit(classInstructions, classEnvironment, this);
                    }

                    return Unit.INSTANCE;
                }
        );
    }

    public InnerClassesAttribute getInnerClassesAttribute(FileEnvironment fileEnvironment) {
        String outerClassId = fileEnvironment.getPackageName() + "." + fileEnvironment.getClassName();
        System.out.println("Outer class id is " + outerClassId);
        ClassDesc outerClassDesc = ClassDesc.of(outerClassId);
        ClassDesc innerClassDesc = ClassDesc.of(outerClassId + "$" + id);

        return InnerClassesAttribute.of(
                InnerClassInfo.of(
                        innerClassDesc,
                        Optional.of(outerClassDesc),
                        Optional.of(id),
                        AccessFlag.PRIVATE, AccessFlag.STATIC
                )
        );
    }
}
