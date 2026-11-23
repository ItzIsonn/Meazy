package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.ast.declareSymbol
import me.itzisonn_.meazy.parser.ast.symbol
import me.itzisonn_.meazy.parser.cfg.ControlFlowGraph
import me.itzisonn_.meazy.parser.cfg.helper.MissingReturnAnalyzer
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
) : ModifierStatement(modifiers), DeclarationStatement<FunctionSymbol> {
    val body = body.toMutableList()

    override val children = body.toSet()

    constructor(
        modifiers: Set<Modifier>, id: String, parameters: List<Parameter>,
        body: List<LocalStatement>, returnDataType: DataType?
    ) : this(
        modifiers, id, null, parameters, body, returnDataType
    )

    context(parents: ParentMap, symbols: SymbolMap)
    override fun declare(environment: Environment) {
        //TODO check whether abstract function isn't overridden
        if (environment !is FunctionDeclarationEnvironment) {
            throw RuntimeException("CANT DECLARE FUNCTION HERE TODO")
        }

        val functionEnvironment = FunctionEnvironment(environment, null, null)
        val symbol = FunctionSymbol(id, parameters, returnDataType, modifiers, functionEnvironment)
        declareSymbol(symbol)
        environment.declareFunction(symbol)

        if (Modifiers.abstract in modifiers) return

        val controlFlowGraph = ControlFlowGraph.build(body)
        if (MissingReturnAnalyzer.hasMissingReturn(controlFlowGraph)) {
            if (returnDataType != null) {
                error("Function with id '$id' doesn't always return a value")
            }

            body.add(ReturnStatement(null))
        }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun resolve(environment: Environment) {
        val symbol = symbol
        symbol.returnDataType?.resolve(environment)
        symbol.parameters.forEach { it.dataType.resolve(environment) }
    }

    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val symbol = symbol
        val functionEnvironment = symbol.environment

        val startLabel = instructions.createLabel()
        val endLabel = instructions.createLabel()
        functionEnvironment.setStartLabel(startLabel)
        functionEnvironment.setEndLabel(endLabel)

        if (functionEnvironment.getParent() is ClassEnvironment) {
            val classSymbol = (functionEnvironment.getParent() as ClassEnvironment).asSymbol()
            val baseClass = classSymbol.baseClass

            baseClass?.environment?.getFunctionRecursively(symbol.id, symbol.parameters.map { it.dataType })?.let { f ->
                if (Modifiers.open !in f.modifiers && Modifiers.abstract !in f.modifiers) {
                    throw RuntimeException("Can't override non-open function $id")
                }
                if (Modifiers.override !in symbol.modifiers) throw RuntimeException(
                    "Must specify override keyword on function $id"
                )
            }

            for (interfaceSymbol in classSymbol.interfaces) {
                interfaceSymbol.environment.getFunctionRecursively(symbol.id, symbol.parameters.map { it.dataType })?.let { f ->
                    if (Modifiers.open !in f.modifiers && Modifiers.abstract !in f.modifiers) {
                        throw RuntimeException("Can't override non-open function $id")
                    }
                    if (Modifiers.override !in symbol.modifiers) throw RuntimeException(
                        "Missing override keyword on function $id"
                    )
                }
            }
        }

        val isShared = Modifiers.shared in modifiers || functionEnvironment.getParent() is FileEnvironment
        val returnDataType = symbol.returnDataType

        val methodTypeDesc = MethodTypeDesc.of(
            returnDataType?.classDesc ?: ConstantDescs.CD_void,
            symbol.parameters.map { it.dataType.classDesc }
        )

        val accessFlags = mutableSetOf<AccessFlag>()
        if (Modifiers.private in symbol.modifiers) accessFlags.add(AccessFlag.PRIVATE)
        else if (Modifiers.protected in symbol.modifiers) accessFlags.add(AccessFlag.PROTECTED)
        else accessFlags.add(AccessFlag.PUBLIC)

        if (isShared) accessFlags.add(AccessFlag.STATIC)
        if (Modifiers.abstract in symbol.modifiers) accessFlags.add(AccessFlag.ABSTRACT)
        else if (Modifiers.open !in symbol.modifiers &&
            !(functionEnvironment.getParent() is ClassEnvironment && (functionEnvironment.getParent() as ClassEnvironment).asSymbol().isInterface)) {
            accessFlags.add(AccessFlag.FINAL)
        }

        instructions.withMethod(
            symbol.id,
            methodTypeDesc,
            accessFlags
        ) {
            initLabel(startLabel)
            initLabel(endLabel)

            for ((id, dataType) in symbol.parameters) {
                val parameterValue = functionEnvironment.declareShadowableVariable(id, dataType, null)

                setLocalName(
                    parameterValue.slot,
                    id,
                    dataType.classDesc,
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
