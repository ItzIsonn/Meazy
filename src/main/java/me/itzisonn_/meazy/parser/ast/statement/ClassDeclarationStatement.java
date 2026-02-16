package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.attribute.InnerClassInfo;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.*;

@Getter
@NullMarked
public class ClassDeclarationStatement extends ModifierStatement implements Statement {
    private final String id;
    private final Set<String> baseClasses;
    private final List<Statement> body;
    private final Map<String, List<Expression>> enumIds;

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
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        if (!(environment instanceof FileEnvironment fileEnvironment)) throw new IllegalArgumentException("Environment must be file TODO");

        boolean isInner = !getModifiers().contains(Modifiers.OPEN());

        ClassDesc classDesc;
        if (isInner) classDesc = ClassDesc.of(fileEnvironment.getPackageName() + "." + fileEnvironment.getClassName() + "$" + id);
        else classDesc = ClassDesc.of(fileEnvironment.getPackageName(), id);

        ClassEnvironment classEnvironment = Registries.CLASS_ENVIRONMENT_FACTORY.getEntry().getValue().create(
                fileEnvironment,
                isInner || modifiers.contains(Modifiers.SHARED()),
                false,
                id,
                baseClasses.stream().findAny().map(ClassDesc::of).orElse(null),
                Set.of(),
                modifiers
        );

        fileEnvironment.declareClass(classEnvironment);

        List<InnerClassesAttribute> attributes = new ArrayList<>();
        int flags = 0;

        if (isInner) attributes.add(getInnerClassesAttribute(fileEnvironment));
        else {
            if (modifiers.contains(Modifiers.OPEN())) flags |= AccessFlag.PUBLIC.mask();
            if (modifiers.contains(Modifiers.FINAL())) flags |= AccessFlag.FINAL.mask();
        }

        instructionsSet.withClass(
                classDesc,
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
                            AccessFlag.PUBLIC.mask(),
                            bodyInstructions -> {
                                bodyInstructions.loadThisReference();

                                bodyInstructions.invokeSuperClass(
                                        ConstantDescs.CD_Object,
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

                                    if (!valueType.equals(variableType)) {
                                        if (NumberType.isNumberType(variableType) && NumberType.isNumberType(valueType)) {
                                            bodyInstructions.convertToNumberType(valueType, variableType);
                                        }

                                        else if (MiscUtils.isBoolean(variableType) && MiscUtils.isBoolean(valueType)) {
                                            bodyInstructions.convertToBooleanType(valueType.isClassOrInterface(), variableType.isClassOrInterface());
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
