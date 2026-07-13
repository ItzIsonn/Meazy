package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.VariableValue
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironmentImpl
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents environment for classes
 */
sealed interface ClassEnvironment : VariableDeclarationEnvironment, FunctionDeclarationEnvironment,
    ConstructorDeclarationEnvironment, ModifieredEnvironment {
    override fun getParent(): ClassDeclarationEnvironment

    /**
     * @return This class environment's id
     */
    val id: String
    val isInterface: Boolean
    override val fullClassName: String
    val classDesc: ClassDesc



    fun getFunctionRecursively(id: String, args: List<DataType>): FunctionEnvironment? {
        var functionEnvironment = getFunction(id, args)
        if (functionEnvironment != null) return functionEnvironment

        val baseClass = baseClass
        if (baseClass != null) {
            val baseClass = resolveClassDesc(baseClass, false)
            val classEnvironment = getClass(baseClass)!!
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args)
            if (functionEnvironment != null) return functionEnvironment
        }

        for (interfaceClassDesc in interfaces) {
            val baseClass: ClassDesc = resolveClassDesc(interfaceClassDesc, false)
            val classEnvironment = getClass(baseClass)!!
            functionEnvironment = classEnvironment.getFunctionRecursively(id, args)
            if (functionEnvironment != null) return functionEnvironment
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
    override val isInterface: Boolean,
    override val id: String,
    baseClass: ClassDesc?,
    interfaces: Set<ClassDesc>,
    unresolvedBaseClasses: Set<String>,
    modifiers: Set<Modifier>
) : ClassEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent, isShared),
    ConstructorDeclarationEnvironment by ConstructorDeclarationEnvironment(parent, isShared),
    VariableDeclarationEnvironmentImpl(parent) {
    override var baseClass = baseClass
        private set
    private val _interfaces = interfaces.toMutableSet()
    private val unresolvedBaseClasses = unresolvedBaseClasses.toMutableSet()
    private val _modifiers = modifiers.toMutableSet()

    constructor(
        parent: ClassDeclarationEnvironment,
        isShared: Boolean,
        isInterface: Boolean,
        id: String,
        unresolvedBaseClasses: Set<String>,
        modifiers: Set<Modifier>
    ) : this(
        parent, isShared, isInterface, id,
        null, setOf(), unresolvedBaseClasses, modifiers
    )



    override fun getParent() = super.getParent() as ClassDeclarationEnvironment
    override val isShared = isShared
    override val interfaces get() = _interfaces.toSet()

    override fun resolveBaseClasses() {
        for (unresolvedBaseClass in unresolvedBaseClasses) {
            val classDesc = getParent().resolveClassDesc(unresolvedBaseClass, false)
            val baseClassEnvironment = getParent().getClass(classDesc)!!

            if (baseClassEnvironment.isInterface) _interfaces.add(baseClassEnvironment.classDesc)
            else {
                if (baseClass != null) throw RuntimeException("Class can't have more than one base class TODO")
                baseClass = baseClassEnvironment.classDesc
            }
        }

        if (baseClass == null && !isInterface) baseClass = ConstantDescs.CD_Object
        unresolvedBaseClasses.clear()
    }



    override fun getVariable(id: String): VariableValue? {
        val variableValue = super<VariableDeclarationEnvironmentImpl>.getVariable(id)
        if (variableValue != null) return variableValue

        val baseClass = baseClass ?: return null
        val baseClassEnvironment = getClass(baseClass.displayName())

        if (baseClassEnvironment != null) return baseClassEnvironment.getVariable(id)
        return null
    }



    override fun getFunction(id: String, args: List<DataType>): FunctionEnvironment? {
        val functionEnvironment = super<FunctionDeclarationEnvironment>.getFunction(id, args)
        if (functionEnvironment != null) return functionEnvironment

        val baseClass = baseClass ?: return null
        val baseClassEnvironment = getClass(baseClass)

        if (baseClassEnvironment != null) return baseClassEnvironment.getFunction(id, args)
        return null
    }



    override val modifiers get() = _modifiers.toSet()

    override val fullClassName: String
        get() {
            val classSpecifier =
                if (Modifiers.private in _modifiers) getClassName()!! + "$"
                else ""

            return getPackageName()!! + "." + classSpecifier + id
        }

    override val classDesc: ClassDesc get() = ClassDesc.of(fullClassName)
}



/** TODO Javadoc for baseclass
 * Creates class environment
 *
 * @param parent Parent
 * @param isShared Whether class environment is shared
 * @param id Id
 * @param modifiers Modifiers
 * @return New class environment
 */
fun ClassEnvironment(
    parent: ClassDeclarationEnvironment, isShared: Boolean, isInterface: Boolean,
    id: String, baseClass: ClassDesc?, interfaces: Set<ClassDesc>, modifiers: Set<Modifier>
): ClassEnvironment = ClassEnvironmentImpl(
    parent, isShared, isInterface,
    id, baseClass, interfaces, setOf(), modifiers
)

/**
 * Creates class environment
 *
 * @param parent Parent
 * @param isShared Whether class environment is shared
 * @param id Id
 * @param modifiers Modifiers
 * @return New class environment
 */
fun ClassEnvironment(
    parent: ClassDeclarationEnvironment, isShared: Boolean, isInterface: Boolean,
    id: String, unresolvedBaseClasses: Set<String>, modifiers: Set<Modifier>
): ClassEnvironment = ClassEnvironmentImpl(
    parent, isShared, isInterface,
    id, unresolvedBaseClasses, modifiers
)