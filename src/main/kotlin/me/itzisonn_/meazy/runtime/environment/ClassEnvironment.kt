package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
import me.itzisonn_.meazy.runtime.data.symbol.GlobalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironmentImpl
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents environment for classes
 */
sealed interface ClassEnvironment : GlobalVariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    override fun getParent(): ClassDeclarationEnvironment

    val fullClassName: String



    fun getFunctionRecursively(id: String, args: List<DataType>): FunctionSymbol? {
        var function = getFunction(id, args)
        if (function != null) return function

        val baseClass = baseClass
        if (baseClass != null) {
            val baseClass = resolveClassDesc(baseClass, false)
            val baseClassSymbol = getClass(baseClass) ?: return null
            function = baseClassSymbol.environment.getFunctionRecursively(id, args)
            if (function != null) return function
        }

        for (interfaceClassDesc in interfaces) {
            val baseClass = resolveClassDesc(interfaceClassDesc, false)
            val baseClassSymbol = getClass(baseClass) ?: return null
            function = baseClassSymbol.environment.getFunctionRecursively(id, args)
            if (function != null) return function
        }

        return null
    }



    /**
     * @return ClassDesc of this class environment's base class
     */
    val baseClass: ClassDesc?

    val interfaces: Set<ClassDesc>

    fun resolveBaseClasses()
}



private class ClassEnvironmentImpl(
    parent: ClassDeclarationEnvironment,
    isShared: Boolean,
    baseClass: ClassDesc?,
    interfaces: Set<ClassDesc>,
    unresolvedBaseClasses: Set<String>
) : ClassEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent, isShared),
    ConstructorDeclarationEnvironment by ConstructorDeclarationEnvironment(parent, isShared),
    GlobalVariableDeclarationEnvironmentImpl(parent) {
    override var baseClass = baseClass
        private set
    private val _interfaces = interfaces.toMutableSet()
    private val unresolvedBaseClasses = unresolvedBaseClasses.toMutableSet()

    constructor(parent: ClassDeclarationEnvironment, isShared: Boolean, unresolvedBaseClasses: Set<String>) :
            this(parent, isShared, null, setOf(), unresolvedBaseClasses)



    override fun getParent() = super.getParent() as ClassDeclarationEnvironment
    override val isShared = isShared
    override val interfaces get() = _interfaces.toSet()

    override fun resolveBaseClasses() {
        for (unresolvedBaseClass in unresolvedBaseClasses) {
            val classDesc = getParent().resolveClassDesc(unresolvedBaseClass, false)
            val baseClassSymbol = getParent().getClass(classDesc) ?: error("Can't find base class $classDesc")

            if (baseClassSymbol.isInterface) _interfaces.add(baseClassSymbol.classDesc)
            else {
                if (baseClass != null) throw RuntimeException("Class can't have more than one base class TODO")
                baseClass = baseClassSymbol.classDesc
            }
        }

        if (baseClass == null && !asSymbol().isInterface) baseClass = ConstantDescs.CD_Object
        unresolvedBaseClasses.clear()
    }



    override fun getVariable(id: String): GlobalVariableSymbol? {
        val variableValue = super<GlobalVariableDeclarationEnvironmentImpl>.getVariable(id)
        if (variableValue != null) return variableValue

        val baseClass = baseClass ?: return null
        val baseClassSymbol = getClass(baseClass.displayName())

        if (baseClassSymbol != null) return baseClassSymbol.environment.getVariable(id)
        return null
    }



    override fun getFunction(id: String, args: List<DataType>): FunctionSymbol? {
        val function = super<FunctionDeclarationEnvironment>.getFunction(id, args)
        if (function != null) return function

        val baseClass = baseClass ?: return null
        val baseClassSymbol = getClass(baseClass)

        if (baseClassSymbol != null) return baseClassSymbol.environment.getFunction(id, args)
        return null
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
 * @param isShared Whether class environment is shared
 * @return New class environment
 */
fun ClassEnvironment(parent: ClassDeclarationEnvironment, isShared: Boolean, baseClass: ClassDesc?, interfaces: Set<ClassDesc>): ClassEnvironment =
    ClassEnvironmentImpl(parent, isShared, baseClass, interfaces, setOf())

/**
 * Creates class environment
 *
 * @param parent Parent
 * @param isShared Whether class environment is shared
 * @return New class environment
 */
fun ClassEnvironment(parent: ClassDeclarationEnvironment, isShared: Boolean, unresolvedBaseClasses: Set<String>): ClassEnvironment =
    ClassEnvironmentImpl(parent, isShared, unresolvedBaseClasses)