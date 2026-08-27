package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironmentImpl
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents file environment
 */
interface FileEnvironment : GlobalVariableDeclarationEnvironment, FunctionDeclarationEnvironment,
    ClassDeclarationEnvironment {
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



private class FileEnvironmentImpl(
    parent: GlobalEnvironment,
    override val packageName: String,
    override val className: String
) : FileEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent, true),
    ClassDeclarationEnvironment by ClassDeclarationEnvironment(parent, true),
    GlobalVariableDeclarationEnvironmentImpl(parent) {
    private val _imports = mutableMapOf<String, ClassDesc>()

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
    override val isShared = true
    override val fullClassName get() = "$packageName.$className"



    override fun addImport(classDesc: ClassDesc, name: String) {
        _imports[name] = classDesc
    }

    override fun addImport(fullName: String) {
        val classDesc = ClassDesc.of(fullName)
        _imports[classDesc.displayName()] = classDesc
    }

    override val imports get() = _imports.toMap()
}



/**
 * Creates file environment
 *
 * @param parent Parent
 * @param packageName Package name
 * @param className Class name
 * @return New file environment
 */
fun FileEnvironment(parent: GlobalEnvironment, packageName: String, className: String): FileEnvironment =
    FileEnvironmentImpl(parent, packageName, className)