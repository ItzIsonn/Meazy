package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
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
    body: List<LocalStatement>,
    val returnDataType: DataType?
) : ModifierStatement(modifiers), DeclarationStatement {
    val body = body.toMutableList()
    private lateinit var symbol: FunctionSymbol

    override val children = body.toSet()

    constructor(
        modifiers: Set<Modifier>, id: String, parameters: List<Parameter>,
        body: List<LocalStatement>, returnDataType: DataType?
    ) : this(
        modifiers, id, null, parameters, body, returnDataType
    )

    context(parents: ParentMap)
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

        symbol = FunctionSymbol(
            id, parameters, returnDataType, modifiers, functionEnvironment
        )
        environment.declareFunction(symbol)

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

    context(parents: ParentMap)
    override fun resolve(environment: Environment) {
        val functionEnvironment = symbol.environment
        functionEnvironment.returnDataType?.resolve(environment)
        functionEnvironment.parameters.forEach { it.dataType.resolve(environment) }
    }

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val functionEnvironment = symbol.environment

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
                    ?.let { f ->
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
                    ?.let { f ->
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
                    null,
                    setOf()
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
                statement.emit(this, functionEnvironment)
            }
            bindLabel(endLabel)
        }
    }
}
