package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.text.translatable
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents file environment
 */
interface FileEnvironment : VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {
    override fun getParent(): GlobalEnvironment


    /**
     * @return Package name of this File environment
     */
    val packageName: String

    /**
     * @return Class name of this File environment
     */
    val className: String

    override val fullClassName: String



    /**
     * Adds to this file environment given import
     */
    fun addImport(classDesc: ClassDesc, name: String)

    /**
     * Adds to this file environment given import
     */
    fun addImport(fullName: String)

    /**
     * @return All imports
     */
    val imports: Map<String, ClassDesc>
}



class FileEnvironmentImpl(
    parent: GlobalEnvironment,
    override val packageName: String,
    override val className: String
) : FunctionDeclarationEnvironmentImpl(parent, true), FileEnvironment {
    private val _imports = mutableMapOf<String, ClassDesc>()
    private val _variables = mutableListOf<VariableValue>()
    private val _classes = mutableSetOf<ClassEnvironment>()

    init {

        addImport("java.lang.String")
        addImport("java.lang.System")
        addImport("java.lang.IO")
        addImport(ConstantDescs.CD_Object, "Any")
        addImport(ConstantDescs.CD_Integer, "Int")
        addImport("java.lang.Long")
        addImport("java.lang.Float")
        addImport("java.lang.Double")
        addImport("java.lang.Boolean")
    }



    override fun getParent() = super.getParent() as GlobalEnvironment
    override val fullClassName get() = "$packageName.$className"



    override fun addImport(classDesc: ClassDesc, name: String) {
        _imports[name] = classDesc
    }

    override fun addImport(fullName: String) {
        val classDesc = ClassDesc.of(fullName)
        _imports[classDesc.displayName()] = classDesc
    }

    override val imports get() = _imports.toMap()



    override fun declareVariable(
        id: String,
        type: DataType,
        isConstant: Boolean,
        value: Expression?
    ): VariableValue {
        val variableValue = VariableValue(id, type, isConstant, setOf(), _variables.size, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override val variables get() = _variables.toList()



    override fun declareClass(classEnvironment: ClassEnvironment) {
        for (otherEnvironment in classes) {
            if (otherEnvironment.id == classEnvironment.id) {
                throw EvaluationException(translatable("meazy:runtime.class.already_exists", classEnvironment.id))
            }
        }

        _classes.add(classEnvironment)
    }

    override val classes get() = _classes.toSet()
}