package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.attribute.InnerClassInfo;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.*;

@Getter
@NullMarked
public class ClassDeclarationStatement extends ModifierStatement implements DeclarationStatement {
    private final String id;
    private final Set<String> baseClasses;
    private final List<Statement> body;
    private final Map<String, List<Expression>> enumIds;
    @Nullable
    private ClassEnvironment classEnvironment;

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body, Map<String, List<Expression>> enumIds) {
        super(modifiers);
        this.id = id;
        this.baseClasses = baseClasses;
        this.body = body;
        this.enumIds = enumIds;
    }

    public ClassDeclarationStatement(Set<Modifier> modifiers, String id, Set<String> baseClasses, List<Statement> body) {
        this(modifiers, id, baseClasses, body, new LinkedHashMap<>());
    }

    @Override
    public void declare(Environment environment) {
        if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
            throw new IllegalArgumentException("Environment must be file TODO");
        }

        boolean isInner = getModifiers().contains(Modifiers.PRIVATE());

        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                classDeclarationEnvironment,
                isInner || modifiers.contains(Modifiers.SHARED()),
                false,
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

        if (!classEnvironment.hasConstructor()) {
            classEnvironment.declareConstructor(Registries.CONSTRUCTOR_ENVIRONMENT_FACTORY.getEntry().getValue().create(classEnvironment, null, null, Set.of(), List.of()));
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

        if (isInner) attributes.add(getInnerClassesAttribute(fileEnvironment));
        else {
            if (modifiers.contains(Modifiers.PRIVATE())) flags.add(AccessFlag.PRIVATE);
            else flags.add(AccessFlag.PUBLIC);

            if (modifiers.contains(Modifiers.ABSTRACT())) flags.add(AccessFlag.ABSTRACT);
            else if (!modifiers.contains(Modifiers.OPEN())) flags.add(AccessFlag.FINAL);
        }

        instructionsSet.withClass(
                classDesc,
                classEnvironment.getBaseClass(),
                classEnvironment.getInterfaces(),
                flags,
                attributes,
                classInstructions -> {
                    boolean hasConstructor = false;
                    for (Statement statement : body) {
                        if (statement instanceof ConstructorDeclarationStatement) hasConstructor = true;
                        statement.emit(classInstructions, classEnvironment, this);
                    }

                    if (!hasConstructor) classInstructions.withConstructor(
                            MethodTypeDesc.of(ConstantDescs.CD_void),
                            Set.of(AccessFlag.PUBLIC),
                            bodyInstructions -> {
                                bodyInstructions.loadThisReference();

                                ClassDesc baseClass = classEnvironment.getBaseClass();
                                if (baseClass == null) throw new RuntimeException("Class " + classEnvironment.getId() + " has no base class");

                                bodyInstructions.invokeSuperClass(
                                        baseClass,
                                        MethodTypeDesc.of(ConstantDescs.CD_void),
                                        _ -> {}
                                );

                                for (VariableValue variableValue : classEnvironment.getVariables()) {
                                    Expression value = variableValue.getInitializer();
                                    if (value == null) continue;

                                    bodyInstructions.loadThisReference();
                                    value.emit(bodyInstructions, classEnvironment, this);

                                    ClassDesc valueType = value.getType(classEnvironment, this).getClassDesc();
                                    ClassDesc variableType = variableValue.getDataType().getClassDesc();

                                    if (!EnvironmentUtils.isInstanceOf(classEnvironment, valueType, variableType)) {
                                        if (!MiscUtils.convertPrimitiveOrBoxed(bodyInstructions, valueType, variableType)) {
                                            throw new RuntimeException("Can't assign value of type " + valueType + " to variable with type " + variableType);
                                        }
                                    }

                                    bodyInstructions.storeField(
                                            classDesc,
                                            variableValue.getId(),
                                            variableValue.getDataType().getClassDesc()
                                    );
                                }

                                bodyInstructions.returnVoid();
                            }
                    );
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
                        AccessFlag.PRIVATE, AccessFlag.STATIC, AccessFlag.FINAL
                )
        );
    }
}
