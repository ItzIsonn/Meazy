package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.text.translatable
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.util.Optional

/**
 * Represents environment for classes
 */
interface ClassEnvironment : VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment, ModifieredEnvironment {
    override fun getParent(): ClassDeclarationEnvironment

    /**
     * @return This class environment's id
     */
    val id: String

    val isInterface: Boolean

    override val fullClassName: String

    val classDesc: ClassDesc


    fun getFunctionRecursively(id: String, args: MutableList<DataType>): Optional<FunctionEnvironment> {
        var functionEnvironment = getFunction(id, args)
        if (functionEnvironment.isPresent) return functionEnvironment

        val baseClass = baseClass
        if (baseClass != null) {
            val baseClass = EnvironmentUtils.resolveClassDesc(this, baseClass, false)
            val classEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass).orElseThrow()
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args)
            if (functionEnvironment.isPresent) return functionEnvironment
        }

        for (interfaceClassDesc in interfaces) {
            val baseClass: ClassDesc = EnvironmentUtils.resolveClassDesc(this, interfaceClassDesc, false)
            val classEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass).orElseThrow()
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args)
            if (functionEnvironment.isPresent) return functionEnvironment
        }

        return Optional.empty<FunctionEnvironment>()
    }


    /**
     * Declares given operator function in this environment
     * @param functionEnvironment Function environment
     */
    fun declareOperatorFunction(functionEnvironment: FunctionEnvironment)

    /**
     * @param id Id
     * @param parameters Parameters
     * @return Declared operator function with given id and args or null
     */
    fun getOperatorFunction(id: String, parameters: List<ClassDesc>): Optional<FunctionEnvironment> {
        main@ for (functionEnvironment in operatorFunctions) {
            if (functionEnvironment.id == id) {
                val functionParameters = functionEnvironment.parameters
                if (parameters.size != functionParameters.size) continue

                for (i in parameters.indices) {
                    val functionParameterClassDesc = functionParameters[i].getDataType().getClassDesc()
                    val parameterClassDesc = parameters[i]
                    if (!EnvironmentUtils.isInstanceOf(
                            this,
                            parameterClassDesc,
                            functionParameterClassDesc
                        )) continue@main
                }

                return Optional.of(functionEnvironment)
            }
        }

        return Optional.empty()
    }

    /**
     * @return All declared operator functions
     */
    val operatorFunctions: Set<FunctionEnvironment>


    /**
     * @return ClassDesc of this class environment's base class
     */
    val baseClass: ClassDesc?

    val interfaces: Set<ClassDesc>

    fun resolveBaseClasses()
}



class ClassEnvironmentImpl : FunctionDeclarationEnvironmentImpl, ClassEnvironment {
    override val id: String
    override val isInterface: Boolean
    private val _variables: MutableList<VariableValue>
    private val _constructors: MutableSet<ConstructorEnvironment>
    override var baseClass: ClassDesc?
        private set
    private val _interfaces: MutableSet<ClassDesc>
    private val unresolvedBaseClasses: MutableSet<String>
    private val _modifiers: MutableSet<Modifier>
    private val _operatorFunctions: MutableSet<FunctionEnvironment>

    constructor(
        parent: ClassDeclarationEnvironment,
        isShared: Boolean,
        isInterface: Boolean,
        id: String,
        baseClass: ClassDesc?,
        interfaces: MutableSet<ClassDesc>,
        modifiers: MutableSet<Modifier>
    ) : super(parent, isShared) {
        this.id = id
        this.isInterface = isInterface
        _variables = mutableListOf()
        _constructors = mutableSetOf()
        this.baseClass = baseClass
        this._interfaces = interfaces
        unresolvedBaseClasses = mutableSetOf()
        this._modifiers = modifiers
        _operatorFunctions = mutableSetOf()
    }

    constructor(
        parent: ClassDeclarationEnvironment,
        isShared: Boolean,
        isInterface: Boolean,
        id: String,
        unresolvedBaseClasses: Set<String>,
        modifiers: Set<Modifier>
    ) : super(parent, isShared) {
        this.id = id
        this.isInterface = isInterface
        _variables = mutableListOf()
        _constructors = mutableSetOf()
        baseClass = null
        _interfaces = mutableSetOf()
        this.unresolvedBaseClasses = unresolvedBaseClasses.toMutableSet()
        this._modifiers = modifiers.toMutableSet()
        _operatorFunctions = mutableSetOf()
    }


    override fun getParent(): ClassDeclarationEnvironment {
        return super.getParent() as ClassDeclarationEnvironment
    }

    override val interfaces: Set<ClassDesc>
        get() = _interfaces.toSet()

    override fun resolveBaseClasses() {
        for (unresolvedBaseClass in unresolvedBaseClasses) {
            val classDesc = EnvironmentUtils.resolveClassDesc(getParent(), unresolvedBaseClass, false)
            val baseClassEnvironment = EnvironmentUtils.getClassEnvironment(getParent(), classDesc).orElseThrow()

            if (baseClassEnvironment.isInterface) _interfaces.add(baseClassEnvironment.classDesc)
            else {
                if (baseClass != null) throw RuntimeException("Class can't have more than one base class TODO")
                baseClass = baseClassEnvironment.classDesc
            }
        }

        if (baseClass == null && !isInterface) baseClass = ConstantDescs.CD_Object
        unresolvedBaseClasses.clear()
    }


    override fun declareVariable(
        id: String,
        type: DataType,
        isConstant: Boolean,
        value: Expression?
    ): VariableValue {
        if (getVariable(id).isPresent) {
            throw EvaluationException(translatable("meazy:runtime.variable.already_exists", id))
        }

        val variableValue = VariableValue(id, type, isConstant, setOf(), _variables.size, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override fun getVariable(id: String): Optional<VariableValue> {
        val variableValue = super.getVariable(id)
        if (variableValue.isPresent) return variableValue

        val baseClass = baseClass ?: return Optional.empty()
        val baseClassEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass.displayName()).orElse(null)

        if (baseClassEnvironment != null) return baseClassEnvironment.getVariable(id)
        return Optional.empty()
    }

    override val variables: List<VariableValue>
        get() = _variables.toList()


    override fun declareOperatorFunction(functionEnvironment: FunctionEnvironment) {
        val parameters = functionEnvironment.parameters

        main@ for (otherFunctionEnvironment in _operatorFunctions) {
            if (otherFunctionEnvironment.id == functionEnvironment.id) {
                val otherParameters = otherFunctionEnvironment.parameters
                if (parameters.size != otherParameters.size) continue

                for (i in parameters.indices) {
                    if (otherParameters[i].getDataType() != parameters[i].getDataType()) continue@main
                }

                throw EvaluationException(
                    translatable(
                        "meazy:runtime.function.operator.already_exists",
                        functionEnvironment.id
                    )
                )
            }
        }

        _operatorFunctions.add(functionEnvironment)
    }

    override val operatorFunctions: Set<FunctionEnvironment>
        get() = _operatorFunctions.toSet()


    override fun getFunction(id: String, args: List<DataType>): Optional<FunctionEnvironment> {
        val functionEnvironment: Optional<FunctionEnvironment> = super<FunctionDeclarationEnvironmentImpl>.getFunction(id, args)
        if (functionEnvironment.isPresent) return functionEnvironment

        if (baseClass == null) return Optional.empty<FunctionEnvironment>()
        val baseClassEnvironment = EnvironmentUtils.getClassEnvironment(this, baseClass!!).orElse(null)

        if (baseClassEnvironment != null) return baseClassEnvironment.getFunction(id, args)
        return Optional.empty<FunctionEnvironment>()
    }


    override fun declareConstructor(constructorEnvironment: ConstructorEnvironment) {
        val parameters = constructorEnvironment.getParameters()

        main@ for (otherConstructorEnvironment in _constructors) {
            val otherParameters = otherConstructorEnvironment.getParameters()
            if (parameters.size != otherParameters.size) continue

            for (i in parameters.indices) {
                if (otherParameters[i].getDataType() != parameters[i].getDataType()) continue@main
            }

            throw EvaluationException(translatable("meazy:runtime.constructor.already_exists"))
        }

        _constructors.add(constructorEnvironment)
    }

    override fun getConstructors(): Set<ConstructorEnvironment> {
        return _constructors.toSet()
    }

    override val modifiers: Set<Modifier>
        get() = _modifiers.toSet()

    override val fullClassName: String
        get() {
            val classSpecifier =
                if (_modifiers.contains(Modifiers.PRIVATE())) EnvironmentUtils.getClassName(this).orElseThrow() + "$"
                else ""

            return EnvironmentUtils.getPackageName(this).orElseThrow() + "." + classSpecifier + id
        }

    override val classDesc: ClassDesc
        get() = ClassDesc.of(fullClassName)
}