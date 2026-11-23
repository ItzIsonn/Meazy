package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
import me.itzisonn_.meazy.runtime.data.symbol.GlobalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironmentImpl
import java.lang.constant.ConstantDescs

/**
 * Represents environment for classes
 */
sealed interface ClassEnvironment : GlobalVariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    override fun getParent(): ClassDeclarationEnvironment

    val fullClassName: String



    fun getFunctionRecursively(id: String, args: List<DataType>): FunctionSymbol? {
        getFunction(id, args)?.let { return@getFunctionRecursively it }
        baseClass?.environment?.getFunctionRecursively(id, args)?.let { return@getFunctionRecursively it }

        for (interfaceSymbol in interfaces) {
            interfaceSymbol.environment.getFunctionRecursively(id, args)?.let { return@getFunctionRecursively it }
        }

        return null
    }



    /**
     * ClassSymbol of this class environment's base class
     */
    val baseClass: ClassSymbol?

    val interfaces: Set<ClassSymbol>

    fun resolveBaseClasses()
}



private class ClassEnvironmentImpl(
    parent: ClassDeclarationEnvironment,
    baseClass: ClassSymbol?,
    interfaces: Set<ClassSymbol>,
    unresolvedBaseClasses: Set<String>
) : ClassEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent),
    ConstructorDeclarationEnvironment by ConstructorDeclarationEnvironment(parent),
    GlobalVariableDeclarationEnvironmentImpl(parent) {
    override var baseClass = baseClass
        private set
    private val _interfaces = interfaces.toMutableSet()
    private val unresolvedBaseClasses = unresolvedBaseClasses.toMutableSet()

    constructor(parent: ClassDeclarationEnvironment, unresolvedBaseClasses: Set<String>) :
            this(parent, null, setOf(), unresolvedBaseClasses)



    override fun getParent() = super.getParent() as ClassDeclarationEnvironment
    override val interfaces get() = _interfaces.toSet()

    override fun resolveBaseClasses() {
        for (unresolvedBaseClass in unresolvedBaseClasses) {
            val classDesc = getParent().resolveClassDesc(unresolvedBaseClass, false)
            val baseClassSymbol = getParent().getClass(classDesc) ?: error("Can't find base class $classDesc")

            if (baseClassSymbol.isInterface) _interfaces.add(baseClassSymbol)
            else {
                if (baseClass != null) throw RuntimeException("Class can't have more than one base class TODO")
                baseClass = baseClassSymbol
            }
        }

        if (baseClass == null && !asSymbol().isInterface) baseClass = getClass(ConstantDescs.CD_Object)
        unresolvedBaseClasses.clear()
    }



    override fun getVariable(id: String): GlobalVariableSymbol? {
        val variableValue = super<GlobalVariableDeclarationEnvironmentImpl>.getVariable(id)
        if (variableValue != null) return variableValue
        return baseClass?.environment?.getVariable(id)
    }



    override fun getFunction(id: String, args: List<DataType>): FunctionSymbol? {
        val function = super<FunctionDeclarationEnvironment>.getFunction(id, args)
        if (function != null) return function
        return baseClass?.environment?.getFunction(id, args)
    }



    override val fullClassName: String
        get() {
            val symbol = asSymbol()

            val classSpecifier =
                if (Modifiers.private in symbol.modifiers) getClassName()!! + "$"
                else ""

            var packageName = getPackageName() ?: ""
            if (packageName.isNotEmpty()) packageName += "."
            return packageName + classSpecifier + symbol.id
        }
}



/** TODO Javadoc for baseclass
 * Creates class environment
 *
 * @param parent Parent
 * @return New class environment
 */
fun ClassEnvironment(parent: ClassDeclarationEnvironment, baseClass: ClassSymbol?, interfaces: Set<ClassSymbol>): ClassEnvironment =
    ClassEnvironmentImpl(parent, baseClass, interfaces, setOf())

/**
 * Creates class environment
 *
 * @param parent Parent
 * @return New class environment
 */
fun ClassEnvironment(parent: ClassDeclarationEnvironment, unresolvedBaseClasses: Set<String>): ClassEnvironment =
    ClassEnvironmentImpl(parent, unresolvedBaseClasses)