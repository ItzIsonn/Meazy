package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironmentImpl

/**
 * Represents file environment
 */
interface FileEnvironment : GlobalVariableDeclarationEnvironment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {
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
     * Adds imports symbol with [name] pointing at [fullName] to this file environment
     */
    fun addImport(name: String, fullName: String)

    /**
     * @return All imports
     */
    val imports: Map<String, String>
}



private class FileEnvironmentImpl(
    parent: GlobalEnvironment,
    override val packageName: String,
    override val className: String
) : FileEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent, true),
    ClassDeclarationEnvironment by ClassDeclarationEnvironment(parent, true),
    GlobalVariableDeclarationEnvironmentImpl(parent) {
    private val _imports = mutableMapOf<String, String>()

    override fun getParent() = super.getParent() as GlobalEnvironment
    override val isShared = true
    override val fullClassName: String
        get() {
            var packageName = packageName
            if (packageName.isNotEmpty()) packageName += "."
            return packageName + className
        }

    override fun addImport(name: String, fullName: String) {
        _imports[name] = fullName
    }

    override val imports get() = _imports.toMap()
}



/**
 * Creates file environment
 *
 * @param parent Parent
 * @param packageName Package name
 * @param className Class name
 *
 * @return New file environment
 */
fun FileEnvironment(parent: GlobalEnvironment, packageName: String, className: String): FileEnvironment = FileEnvironmentImpl(parent, packageName, className)