package me.itzisonn_.meazy.instruction;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.array.NewReferenceArrayInstruction;
import me.itzisonn_.meazy.instruction.array.StoreReferenceIntoArrayInstruction;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfEqualsZeroInstruction;
import me.itzisonn_.meazy.instruction.field.GetFieldInstruction;
import me.itzisonn_.meazy.instruction.field.StoreFieldInstruction;
import me.itzisonn_.meazy.instruction.field.WithFieldInstruction;
import me.itzisonn_.meazy.instruction.local.SetLocalNameInstruction;
import me.itzisonn_.meazy.instruction.method.*;
import me.itzisonn_.meazy.instruction.label.BindLabelInstruction;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfNonNullInstruction;
import me.itzisonn_.meazy.instruction.label.GotoLabelInstruction;
import me.itzisonn_.meazy.instruction.label.InitLabelInstruction;
import me.itzisonn_.meazy.instruction.stack.LoadConstantInstruction;
import me.itzisonn_.meazy.instruction.local.GetLocalInstruction;
import me.itzisonn_.meazy.instruction.stack.LoadThisReferenceInstruction;
import me.itzisonn_.meazy.instruction.local.StoreLocalInstruction;
import me.itzisonn_.meazy.instruction.misc.CheckCastInstruction;
import me.itzisonn_.meazy.instruction.misc.InstanceOfInstruction;
import me.itzisonn_.meazy.instruction.misc.ReturnInstruction;
import me.itzisonn_.meazy.instruction.misc.WithClassInstruction;
import me.itzisonn_.meazy.instruction.number.*;
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation;
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation;
import me.itzisonn_.meazy.instruction.stack.DuplicateInstruction;
import me.itzisonn_.meazy.instruction.stack.PopInstruction;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.*;
import java.lang.reflect.AccessFlag;
import java.util.*;
import java.util.function.Consumer;

@NullMarked
public class InstructionsSet {
    private final List<Instruction> instructions = new ArrayList<>();
    @Getter
    private final BytecodeBuilders bytecodeBuilders;

    public InstructionsSet(BytecodeBuilders bytecodeBuilders) {
        this.bytecodeBuilders = bytecodeBuilders;
    }

    public List<Instruction> getInstructions() {
        return new ArrayList<>(instructions);
    }

    public void with(Instruction instruction) {
        instructions.add(instruction);
    }



    public void withField(String id, ClassDesc type, Set<AccessFlag> flags) {
        with(new WithFieldInstruction(id, type, toIntFlags(flags)));
    }

    public void getField(ClassDesc owner, String id, ClassDesc type) {
        with(new GetFieldInstruction(owner, id, type, false));
    }

    public void getStaticField(ClassDesc owner, String id, ClassDesc type) {
        with(new GetFieldInstruction(owner, id, type, true));
    }

    public void storeField(ClassDesc owner, String id, ClassDesc type) {
        with(new StoreFieldInstruction(owner, id, type, false));
    }

    public void storeStaticField(ClassDesc owner, String id, ClassDesc type) {
        with(new StoreFieldInstruction(owner, id, type, true));
    }



    public void withMethod(String id, MethodTypeDesc methodTypeDesc, Set<AccessFlag> flags, Consumer<InstructionsSet> bodyInstructions) {
        with(new WithMethodInstruction(id, methodTypeDesc, toIntFlags(flags), bodyInstructions));
    }

    public void withConstructor(MethodTypeDesc methodTypeDesc, Set<AccessFlag> flags, Consumer<InstructionsSet> bodyInstructions) {
        with(new WithConstructorInstruction(methodTypeDesc, toIntFlags(flags), bodyInstructions));
    }

    public void withClass(ClassDesc classDesc, @Nullable ClassDesc superClass, Set<ClassDesc> interfaceClasses, Set<AccessFlag> flags, List<InnerClassesAttribute> attributes, Consumer<InstructionsSet> classInstructions) {
        with(new WithClassInstruction(classDesc, superClass, interfaceClasses, toIntFlags(flags), attributes, classInstructions));
    }



    public void invokeMethod(ClassDesc owner, String id, MethodTypeDesc methodTypeDesc, Consumer<InstructionsSet> argsInstructions, InvokeType invokeType) {
        with(new InvokeMethodInstruction(owner, id, methodTypeDesc, argsInstructions, invokeType));
    }

    public void invokeDynamicMethod(DirectMethodHandleDesc bootstrapMethod, String id, MethodTypeDesc methodTypeDesc, ConstantDesc... args) {
        with(new InvokeDynamicMethodInstruction(bootstrapMethod, id, methodTypeDesc, List.of(args)));
    }

    public void invokeConstructor(ClassDesc owner, MethodTypeDesc constructorTypeDesc, Consumer<InstructionsSet> argsInstructions) {
        with(new InvokeConstructorInstruction(owner, constructorTypeDesc, argsInstructions, false));
    }

    public void invokeSuperClass(ClassDesc owner, MethodTypeDesc constructorTypeDesc, Consumer<InstructionsSet> argsInstructions) {
        with(new InvokeConstructorInstruction(owner, constructorTypeDesc, argsInstructions, true));
    }



    public void loadConstant(@Nullable ConstantDesc constant) {
        with(new LoadConstantInstruction(constant));
    }

    public void loadConstant(boolean constant) {
        loadConstant(constant ? 1 : 0);
    }

    public void loadNull() {
        loadConstant(null);
    }

    public void loadThisReference() {
        with(new LoadThisReferenceInstruction());
    }



    public void getLocal(ClassDesc type, int slot) {
        with(new GetLocalInstruction(type, slot));
    }

    public void storeLocal(ClassDesc type, int slot) {
        with(new StoreLocalInstruction(type, slot));
    }

    public void setLocalName(int slot, String id, ClassDesc type, UUID startLabelUuid, UUID endLabelUuid) {
        with(new SetLocalNameInstruction(slot, id, type, startLabelUuid, endLabelUuid));
    }



    public void newReferenceArray(ClassDesc type) {
        with(new NewReferenceArrayInstruction(type));
    }

    public void storeReferenceIntoArray() {
        with(new StoreReferenceIntoArrayInstruction());
    }



    public void negateNumber(NumberType type) {
        with(new NegateNumberInstruction(type));
    }

    public void negateNumber(ClassDesc classDesc) {
        negateNumber(getNullSafeNumberType(classDesc));
    }

    public void convertToNumberType(NumberType from, NumberType to) {
        with(new ConvertToNumberTypeInstruction(from, to));
    }

    public void convertToNumberType(ClassDesc from, ClassDesc to) {
        convertToNumberType(getNullSafeNumberType(from), getNullSafeNumberType(to));
    }

    public void convertToNumberType(NumberType from, ClassDesc to) {
        convertToNumberType(from, getNullSafeNumberType(to));
    }

    public void convertToNumberType(ClassDesc from, NumberType to) {
        convertToNumberType(getNullSafeNumberType(from), to);
    }

    public void convertToBooleanType(boolean isFromBoxed, boolean isToBoxed) {
        with(new ConvertToBooleanTypeInstruction(isFromBoxed, isToBoxed));
    }

    public void arithmeticOperation(NumberType type, ArithmeticOperation operation) {
        with(new ArithmeticOperationInstruction(type, operation));
    }

    public void arithmeticOperation(ClassDesc type, ArithmeticOperation operation) {
        arithmeticOperation(getNullSafeNumberType(type), operation);
    }

    public void logicalOperation(LogicalOperation operation) {
        with(new LogicalOperationInstruction(operation));
    }



    public void instanceOf(ClassDesc target) {
        with(new InstanceOfInstruction(target));
    }

    public void checkCast(ClassDesc type) {
        with(new CheckCastInstruction(type));
    }

    public void returnValue(@Nullable ClassDesc classDesc) {
        with(new ReturnInstruction(classDesc));
    }

    public void returnVoid() {
        returnValue(null);
    }



    public void duplicate() {
        with(new DuplicateInstruction());
    }

    public void pop() {
        with(new PopInstruction());
    }



    public UUID createLabel() {
        UUID uuid = UUID.randomUUID();
        bytecodeBuilders.addLabel(uuid);
        return uuid;
    }

    public void initLabel(UUID uuid) {
        with(new InitLabelInstruction(uuid));
    }

    public UUID createAndInitLabel() {
        UUID uuid = createLabel();
        initLabel(uuid);
        return uuid;
    }

    public void bindLabel(UUID uuid) {
        with(new BindLabelInstruction(uuid));
    }

    public void gotoLabel(UUID uuid) {
        with(new GotoLabelInstruction(uuid));
    }



    public void gotoLabelIfNonNull(UUID uuid) {
        with(new GotoLabelIfNonNullInstruction(uuid));
    }

    public void gotoLabelIfEqualsZero(UUID uuid) {
        with(new GotoLabelIfEqualsZeroInstruction(uuid));
    }

    public void gotoLabelIfComparisonTrue(NumberType type, ComparisonOperation operation, UUID uuid) {
        with(new GotoLabelIfComparisonTrueInstruction(type, operation, uuid));
    }

    public void gotoLabelIfComparisonTrue(ClassDesc type, ComparisonOperation operation, UUID uuid) {
        gotoLabelIfComparisonTrue(getNullSafeNumberType(type), operation, uuid);
    }



    private NumberType getNullSafeNumberType(ClassDesc type) {
        NumberType numberType = NumberType.valueOf(type);
        if (numberType == null) throw new IllegalArgumentException("There's no NumberType corresponding to ClassDesc " +  type);
        return numberType;
    }

    private static int toIntFlags(Collection<AccessFlag> accessFlags) {
        return accessFlags.stream().map(AccessFlag::mask).reduce((i1, i2) -> i1 | i2).orElse(0);
    }

    private static int toIntFlags(AccessFlag... accessFlags) {
        return toIntFlags(Arrays.asList(accessFlags));
    }
}
