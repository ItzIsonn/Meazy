package me.itzisonn_.meazy.instruction

import me.itzisonn_.meazy.instruction.array.NewReferenceArrayInstruction
import me.itzisonn_.meazy.instruction.array.StoreReferenceIntoArrayInstruction
import me.itzisonn_.meazy.instruction.field.GetFieldInstruction
import me.itzisonn_.meazy.instruction.field.StoreFieldInstruction
import me.itzisonn_.meazy.instruction.field.WithFieldInstruction
import me.itzisonn_.meazy.instruction.label.*
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation
import me.itzisonn_.meazy.instruction.local.GetLocalInstruction
import me.itzisonn_.meazy.instruction.local.SetLocalNameInstruction
import me.itzisonn_.meazy.instruction.local.StoreLocalInstruction
import me.itzisonn_.meazy.instruction.method.*
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.instruction.misc.CastInstruction
import me.itzisonn_.meazy.instruction.misc.InstanceOfInstruction
import me.itzisonn_.meazy.instruction.misc.ReturnInstruction
import me.itzisonn_.meazy.instruction.misc.WithClassInstruction
import me.itzisonn_.meazy.instruction.number.*
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation
import me.itzisonn_.meazy.instruction.stack.DuplicateInstruction
import me.itzisonn_.meazy.instruction.stack.LoadConstantInstruction
import me.itzisonn_.meazy.instruction.stack.LoadThisReferenceInstruction
import me.itzisonn_.meazy.instruction.stack.PopInstruction
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag
import kotlin.uuid.Uuid

class InstructionsSet(private val bytecodeBuilders: BytecodeBuilders) {
    private val _instructions: MutableList<Instruction> = mutableListOf()
    val instructions get() = _instructions.toList()

    fun with(instruction: Instruction) {
        _instructions.add(instruction)
    }



    fun withField(id: String, type: ClassDesc, flags: Set<AccessFlag>) {
        with(WithFieldInstruction(id, type, toIntFlags(flags)))
    }

    fun getField(owner: ClassDesc, id: String, type: ClassDesc) {
        with(GetFieldInstruction(owner, id, type, false))
    }

    fun getStaticField(owner: ClassDesc, id: String, type: ClassDesc) {
        with(GetFieldInstruction(owner, id, type, true))
    }

    fun storeField(owner: ClassDesc, id: String, type: ClassDesc) {
        with(StoreFieldInstruction(owner, id, type, false))
    }

    fun storeStaticField(owner: ClassDesc, id: String, type: ClassDesc) {
        with(StoreFieldInstruction(owner, id, type, true))
    }



    fun withMethod(
        id: String, methodTypeDesc: MethodTypeDesc,
        flags: Set<AccessFlag>, bodyInstructions: InstructionsSet.() -> Unit
    ) {
        with(WithMethodInstruction(
            id, methodTypeDesc, toIntFlags(flags), bodyInstructions
        ))
    }

    fun withConstructor(methodTypeDesc: MethodTypeDesc, flags: Set<AccessFlag>, bodyInstructions: InstructionsSet.() -> Unit) {
        with(WithConstructorInstruction(
            methodTypeDesc, toIntFlags(flags), bodyInstructions
        ))
    }

    fun withClass(
        classDesc: ClassDesc, superClass: ClassDesc?, interfaceClasses: Set<ClassDesc>,
        flags: Set<AccessFlag>, attributes: List<InnerClassesAttribute>, classInstructions: InstructionsSet.() -> Unit
    ) {
        with(WithClassInstruction(
            classDesc, superClass, interfaceClasses,
            toIntFlags(flags), attributes, classInstructions
        ))
    }



    fun invokeMethod(
        owner: ClassDesc, id: String, methodTypeDesc: MethodTypeDesc,
        invokeType: InvokeType, argsInstructions: InstructionsSet.() -> Unit = {}
    ) {
        with(InvokeMethodInstruction(
            owner, id, methodTypeDesc, argsInstructions, invokeType
        ))
    }

    fun invokeDynamicMethod(
        bootstrapMethod: DirectMethodHandleDesc, id: String,
        methodTypeDesc: MethodTypeDesc, vararg args: ConstantDesc
    ) {
        with(InvokeDynamicMethodInstruction(
            bootstrapMethod, id, methodTypeDesc, listOf(*args)
        ))
    }

    fun invokeConstructor(
        owner: ClassDesc, constructorTypeDesc: MethodTypeDesc,
        argsInstructions: InstructionsSet.() -> Unit = {}
    ) {
        with(InvokeConstructorInstruction(
            owner, constructorTypeDesc, argsInstructions, false
        ))
    }

    fun invokeSuperClass(
        owner: ClassDesc, constructorTypeDesc: MethodTypeDesc,
        argsInstructions: InstructionsSet.() -> Unit = {}
    ) {
        with(InvokeConstructorInstruction(
            owner, constructorTypeDesc, argsInstructions, true
        ))
    }



    fun loadConstant(constant: Int) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: Long) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: Float) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: Double) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: Boolean) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: String) = with(LoadConstantInstruction(constant))
    fun loadConstant(constant: ClassDesc) = with(LoadConstantInstruction(constant))
    fun loadNull() = with(LoadConstantInstruction())

    fun loadThisReference() = with(LoadThisReferenceInstruction())



    fun getLocal(type: ClassDesc, slot: Int) {
        with(GetLocalInstruction(type, slot))
    }

    fun storeLocal(type: ClassDesc, slot: Int) {
        with(StoreLocalInstruction(type, slot))
    }

    fun setLocalName(slot: Int, id: String, type: ClassDesc, startLabelUuid: Uuid, endLabelUuid: Uuid) {
        with(SetLocalNameInstruction(slot, id, type, startLabelUuid, endLabelUuid))
    }



    fun newReferenceArray(type: ClassDesc) {
        with(NewReferenceArrayInstruction(type))
    }

    fun storeReferenceIntoArray() {
        with(StoreReferenceIntoArrayInstruction())
    }



    fun negateNumber(type: NumberType) {
        with(NegateNumberInstruction(type))
    }

    fun convertToNumberType(from: NumberType, to: NumberType) {
        with(ConvertToNumberTypeInstruction(from, to))
    }

    fun convertToBooleanType(isFromBoxed: Boolean, isToBoxed: Boolean) {
        with(ConvertToBooleanTypeInstruction(isFromBoxed, isToBoxed))
    }

    fun arithmeticOperation(type: NumberType, operation: ArithmeticOperation) {
        with(ArithmeticOperationInstruction(type, operation))
    }

    fun logicalOperation(operation: LogicalOperation) {
        with(LogicalOperationInstruction(operation))
    }



    fun instanceOf(target: ClassDesc) {
        with(InstanceOfInstruction(target))
    }

    fun cast(type: ClassDesc) {
        with(CastInstruction(type))
    }

    fun returnValue(classDesc: ClassDesc?) {
        with(ReturnInstruction(classDesc))
    }

    fun returnVoid() {
        returnValue(null)
    }



    fun duplicate() {
        with(DuplicateInstruction())
    }

    fun pop() {
        with(PopInstruction())
    }



    fun createLabel(): Uuid {
        val uuid = Uuid.random()
        bytecodeBuilders.addLabel(uuid)
        return uuid
    }

    fun initLabel(uuid: Uuid) {
        with(InitLabelInstruction(uuid))
    }

    fun createAndInitLabel(): Uuid {
        val uuid = createLabel()
        initLabel(uuid)
        return uuid
    }

    fun bindLabel(uuid: Uuid) {
        with(BindLabelInstruction(uuid))
    }

    fun gotoLabel(uuid: Uuid) {
        with(GotoLabelInstruction(uuid))
    }



    fun gotoLabelIfNonNull(uuid: Uuid) {
        with(GotoLabelIfNonNullInstruction(uuid))
    }

    fun gotoLabelIfEqualsZero(uuid: Uuid) {
        with(GotoLabelIfEqualsZeroInstruction(uuid))
    }

    fun gotoLabelIfNotEqualsZero(uuid: Uuid) {
        with(GotoLabelIfNotEqualsZeroInstruction(uuid))
    }

    fun gotoLabelIfComparisonTrue(type: NumberType, operation: ComparisonOperation, uuid: Uuid) {
        with(GotoLabelIfComparisonTrueInstruction(type, operation, uuid))
    }



    companion object {
        private fun toIntFlags(accessFlags: Collection<AccessFlag>): Int {
            return accessFlags
                .map { it.mask() }
                .reduceOrNull { i1, i2 -> i1 or i2 } ?: 0
        }
    }
}



@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
val String.java get() = this as java.lang.String