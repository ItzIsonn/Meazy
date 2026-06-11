package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.Parameter
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.*
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class FunctionDeclarationStatement(
    modifiers: Set<Modifier>,
    val id: String,
    val classId: String?,
    val parameters: List<Parameter>,
    val body: MutableList<LocalStatement>,
    val returnDataType: DataType?,
    val returnDataTypeValue: Expression?
) : ModifierStatement(modifiers), DeclarationStatement {
    private lateinit var functionEnvironment: FunctionEnvironment

    constructor(
        modifiers: Set<Modifier>, id: String, parameters: List<Parameter>,
        body: MutableList<LocalStatement>, returnDataType: DataType?
    ) : this(
        modifiers, id, null, parameters, body,
        returnDataType, null
    )

    override fun declare(environment: Environment) {
        //TODO check whether abstract function isn't overridden
        if (environment !is FunctionDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE FUNCTION HERE TODO")
        }

        val isShared = Modifiers.shared in modifiers || environment is FileEnvironment

        val functionEnvironment = FunctionEnvironment(
            environment, null, null, id, parameters,
            returnDataType, isShared, modifiers
        )

        environment.declareFunction(functionEnvironment)
        this.functionEnvironment = functionEnvironment

        if (Modifiers.abstract in modifiers) return

        for (localStatement in body) {
            if (localStatement.alwaysReturns()) return
        }

        if (returnDataType == null) {
            body.add(ReturnStatement(null))
            return
        }

        throw RuntimeException("Function with id $id doesn't always return a value")
    }

    override fun resolve(environment: Environment) {
        val returnDataType: DataType?
        if (functionEnvironment.returnDataType != null) returnDataType = functionEnvironment.returnDataType
        else if (returnDataTypeValue != null) {
            returnDataType = returnDataTypeValue.getType(environment, this)
            functionEnvironment.returnDataType = returnDataType
        }
        else returnDataType = null

        returnDataType?.resolve(environment)
        functionEnvironment.parameters.forEach { it.dataType.resolve(environment) }
    }

    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val startLabel = instructions.createLabel()
        val endLabel = instructions.createLabel()
        functionEnvironment.setStartLabel(startLabel)
        functionEnvironment.setEndLabel(endLabel)

        if (functionEnvironment.getParent() is ClassEnvironment) {
            val classEnvironment = functionEnvironment.getParent() as ClassEnvironment
            val baseClass = classEnvironment.baseClass

            if (baseClass != null) {
                val cls = classEnvironment.getClass(baseClass)
                    ?: error("Unknown base class $baseClass")

                cls.getFunctionRecursively(
                    functionEnvironment.id, functionEnvironment.parameters.map { it.dataType }
                )
                    .ifPresent { f ->
                        if (Modifiers.open !in f.modifiers && Modifiers.abstract !in f.modifiers) {
                            throw RuntimeException("Can't override non-open function $id")
                        }
                        if (Modifiers.override !in functionEnvironment.modifiers) throw RuntimeException(
                            "Must specify override keyword on function $id"
                        )
                    }
            }

            for (interfaceClassDesc in classEnvironment.interfaces) {
                val cls = classEnvironment.getClass(interfaceClassDesc)
                    ?: error("Unknown interface $interfaceClassDesc")

                cls.getFunctionRecursively(
                    functionEnvironment.id, functionEnvironment.parameters.map { it.dataType }
                )
                    .ifPresent { f ->
                        if (Modifiers.open !in f.modifiers && Modifiers.abstract !in f.modifiers) {
                            throw RuntimeException("Can't override non-open function $id")
                        }
                        if (Modifiers.override !in functionEnvironment.modifiers) throw RuntimeException(
                            "Must specify override keyword on function $id"
                        )
                    }
            }
        }

        val isShared = functionEnvironment.isShared
        val returnDataType = functionEnvironment.returnDataType

        val methodTypeDesc = MethodTypeDesc.of(
            returnDataType?.classDesc ?: ConstantDescs.CD_void,
            functionEnvironment.parameters.map { it.dataType.classDesc }
        )

        val accessFlags = mutableSetOf<AccessFlag>()
        if (Modifiers.private in functionEnvironment.modifiers) accessFlags.add(AccessFlag.PRIVATE)
        else if (Modifiers.protected in functionEnvironment.modifiers) accessFlags.add(AccessFlag.PROTECTED)
        else accessFlags.add(AccessFlag.PUBLIC)

        if (isShared) accessFlags.add(AccessFlag.STATIC)
        if (Modifiers.abstract in functionEnvironment.modifiers) accessFlags.add(AccessFlag.ABSTRACT)
        else if (Modifiers.open !in functionEnvironment.modifiers &&
            !(functionEnvironment.getParent() is ClassEnvironment && (functionEnvironment.getParent() as ClassEnvironment).isInterface)) {
            accessFlags.add(AccessFlag.FINAL)
        }

        instructions.withMethod(
            functionEnvironment.id,
            methodTypeDesc,
            accessFlags
        ) {
            initLabel(startLabel)
            initLabel(endLabel)

            for (parameter in functionEnvironment.parameters) {
                val parameterValue = functionEnvironment.declareVariable(
                    parameter.id,
                    parameter.dataType,
                    parameter.isConstant,
                    null
                )

                setLocalName(
                    parameterValue.slot,
                    parameter.id,
                    parameter.dataType.classDesc,
                    startLabel,
                    endLabel
                )
            }

            bindLabel(startLabel)
            for (statement in body) {
                statement.emit(this, functionEnvironment, this@FunctionDeclarationStatement)
            }
            bindLabel(endLabel)
        }
    }
}
