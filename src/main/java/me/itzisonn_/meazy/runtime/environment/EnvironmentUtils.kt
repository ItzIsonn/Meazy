package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.runtime.environment.declaration.ClassDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Searches for given environment as a parent in this environment and all parents
 * @param target Environment to lookup
 * @return Whether this environment has requested parent
 */
fun Environment.hasParent(target: Environment): Boolean {
    val parent = getParent()
    return target == parent || (parent != null && parent.hasParent(target))
}

/**
 * Searches for environment that matches given predicate in all parents of given environment
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment has requested parent
 */
fun Environment.hasParent(predicate: (Environment?) -> Boolean): Boolean {
    val parent = getParent()
    return predicate(parent) || parent != null && parent.hasParent(predicate)
}

/**
 * Searches for environment that matches given predicate in given environment and all of its parents
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment has requested parent
 */
fun Environment.hasParentOrSelf(predicate: (Environment?) -> Boolean): Boolean {
    if (predicate(this)) return true
    return hasParent(predicate)
}

/**
 * Searches for environment that is instance of given class in all parents of given environment
 * @param cls Class of parent environment
 * @return Whether this environment has parent of given class
 */
fun <T : Environment> Environment.hasParent(cls: KClass<T>): Boolean {
    val parent = getParent()
    if (cls.isInstance(parent)) return true
    return parent?.hasParent(cls) ?: false
}

/**
 * Searches for environment that is instance of given class in all parents of given environment
 * @param T Type of parent environment
 * @return Whether this environment has parent of given class
 */
inline fun <reified T : Environment> Environment.hasParent() = hasParent(T::class)

/**
 * Searches for environment that is instance of given class in given environment and all of its parents
 * @param cls Class of parent environment
 * @return Whether this environment or its parent is instance of given class
 */
fun <T : Environment> Environment.hasParentOrSelf(cls: KClass<T>): Boolean {
    if (cls.isInstance(this)) return true
    return hasParent(cls)
}

/**
 * Searches for environment that is instance of given class in given environment and all of its parents
 * @param T Type of parent environment
 * @return Whether this environment or its parent is instance of given class
 */
inline fun <reified T : Environment> Environment.hasParentOrSelf() = hasParentOrSelf(T::class)



/**
 * Searches for environment that matches given predicate in all parents of given environment
 * @param predicate Predicate that matches parent environment
 * @return Parent that matches given predicate or null
 */
fun Environment.getParent(predicate: (Environment?) -> Boolean): Environment? {
    val parent = getParent()
    if (predicate(parent)) return parent
    return parent?.getParent(predicate)
}

/**
 * Searches for environment that matches given predicate in given environment and all of its parents
 * @param predicate Predicate that matches parent environment
 * @return Parent or given environment that matches given predicate or null
 */
fun Environment.getParentOrSelf(predicate: (Environment?) -> Boolean): Environment? {
    if (predicate(this)) return this
    return getParent(predicate)
}

/**
 * Searches for environment that is instance of given class in all parents of given environment
 * @param cls Class of parent environment
 * @return Parent of given class or null
 */
fun <T : Environment> Environment.getParent(cls: KClass<T>): T? {
    val parent = getParent() ?: return null
    if (cls.isInstance(parent)) return cls.cast(parent)
    return parent.getParent(cls)
}

/**
 * Searches for environment that is instance of given class in all parents of given environment
 * @param T Type of parent environment
 * @return Parent of given class or null
 */
inline fun <reified T : Environment> Environment.getParent() = getParent(T::class)

/**
 * Searches for environment that is instance of given class in given environment and all of its parents
 * @param cls Class of parent environment
 * @return Parent or given environment of given class or null
 */
fun <T : Environment> Environment.getParentOrSelf(cls: KClass<T>): T? {
    if (cls.isInstance(this)) return cls.cast(this)
    return getParent(cls)
}

/**
 * Searches for environment that is instance of given class in given environment and all of its parents
 * @param T Type of parent environment
 * @return Parent or given environment of given class or null
 */
inline fun <reified T : Environment> Environment.getParentOrSelf() = getParentOrSelf(T::class)



/**
 * TODO
 */
fun Environment.isInstanceOf(classDesc: ClassDesc, target: ClassDesc): Boolean {
    if (classDesc == target) return true
    val classEnvironment = getClass(classDesc) ?: return false

    val baseClass = classEnvironment.baseClass
    if (baseClass != null && isInstanceOf(baseClass, target)) {
        return true
    }

    for (interfaceClassDesc in classEnvironment.interfaces) {
        if (isInstanceOf(interfaceClassDesc, target)) return true
    }

    return false
}

/**
 * TODO
 */
fun Environment.getCommonOf(classDesc1: ClassDesc, classDesc2: ClassDesc): ClassDesc? {
    if (classDesc1 == classDesc2) return classDesc1
    if (isInstanceOf(classDesc1, classDesc2)) return classDesc2
    if (isInstanceOf(classDesc2, classDesc1)) return classDesc1

    val classEnvironment = getClass(classDesc1) ?: return null
    var gotObjectAsCommon = false

    val baseClass = classEnvironment.baseClass
    if (baseClass != null) {
        val commonClassDesc = getCommonOf(baseClass, classDesc2)
        if (ConstantDescs.CD_Object == commonClassDesc) gotObjectAsCommon = true
        else if (commonClassDesc != null) return commonClassDesc
    }

    for (interfaceClassDesc in classEnvironment.interfaces) {
        val commonClassDesc = getCommonOf(interfaceClassDesc, classDesc2)
        if (commonClassDesc != null) return commonClassDesc
    }

    if (gotObjectAsCommon) return ConstantDescs.CD_Object
    return null
}

/**
 * TODO
 */
fun Environment.resolveClassDesc(classDesc: ClassDesc, allowPrimitives: Boolean): ClassDesc {
    if (classDesc.isPrimitive || classDesc.isArray) return classDesc
    if (!classDesc.packageName().isEmpty()) return classDesc

    val fileEnvironment = getParentOrSelf<FileEnvironment>() ?: return classDesc

    var classEnvironment = fileEnvironment.getClass(classDesc.displayName()).orElse(null)
    if (classEnvironment != null) return classEnvironment.classDesc

    classEnvironment = getClass(classDesc)
    if (classEnvironment != null) return classEnvironment.classDesc

    val fullId: String
    val fullClassDesc = fileEnvironment.imports[classDesc.displayName()]
    if (fullClassDesc != null) {
        if (fullClassDesc.isPrimitive || classDesc.isArray) return fullClassDesc
        fullId = (if (fullClassDesc.packageName().isEmpty()) ""
        else fullClassDesc.packageName() + ".") + fullClassDesc.displayName()
    }
    else fullId = classDesc.displayName()

    if (allowPrimitives) {
        if (classDesc.displayName() == "Int") return ConstantDescs.CD_int
        if (classDesc.displayName() == "Long") return ConstantDescs.CD_long
        if (classDesc.displayName() == "Float") return ConstantDescs.CD_float
        if (classDesc.displayName() == "Double") return ConstantDescs.CD_double
        if (classDesc.displayName() == "Boolean") return ConstantDescs.CD_boolean
    }

    classEnvironment = getClass(fullId)
    if (classEnvironment != null) return classEnvironment.classDesc

    val resolvedClassDesc = ClassDesc.of(fullId)
    fileEnvironment.getParent().resolveJavaClass(resolvedClassDesc)
    return resolvedClassDesc
}

/**
 * TODO
 */
fun Environment.resolveClassDesc(classDesc: String, allowPrimitives: Boolean): ClassDesc {
    return resolveClassDesc(ClassDesc.of(classDesc), allowPrimitives)
}



/**
 * @return File environment in parent environments of given environment
 */
fun Environment.getFileEnvironment(): FileEnvironment? {
    return getParentOrSelf<FileEnvironment>()
}

/**
 * @return Class name of this environment
 */
fun Environment.getClassName(): String? {
    return getFileEnvironment()?.className
}

/**
 * @return Package name of this environment
 */
fun Environment.getPackageName(): String? {
    return getFileEnvironment()?.packageName
}

/**
 * @return Whether two given environments are from same package
 */
fun areFromSamePackage(environment1: Environment, environment2: Environment): Boolean {
    return environment1.getPackageName() == environment2.getPackageName()
}



/**
 * Searches for variable with given id in this environment and all parents
 *
 * @param id Variable's id
 * @return Environment that has requested variable or null
 */
fun Environment.getVariable(id: String): VariableValue? {
    if (this is VariableDeclarationEnvironment) {
        val variableValue = getVariable(id)
        if (variableValue.isPresent) return variableValue.get()
    }

    if (this is GlobalEnvironment) {
        for (fileEnvironment in fileEnvironments) {
            val variableValue = fileEnvironment.getVariable(id)
            if (variableValue.isPresent) return variableValue.get()
        }

        return null
    }

    return getParent()?.getVariable(id)
}

/**
 * Searches for variable with given id in this environment and all parents
 *
 * @param id Variable's id
 * @return Environment that has requested variable or null
 */
fun Environment.getVariableDeclarationEnvironment(id: String): VariableDeclarationEnvironment? {
    return getVariable(id)?.parentEnvironment
}



/**
 * Searches for function with given id and args in this environment and all parents
 *
 * @param id Function's id
 * @param args Function's args
 * @return Environment that has requested function or null
 */
fun Environment.getFunction(id: String, args: List<DataType>): FunctionEnvironment? {
    if (this is FunctionDeclarationEnvironment) {
        val functionEnvironment = getFunction(id, args)
        if (functionEnvironment.isPresent) return functionEnvironment.get()
    }

    if (this is GlobalEnvironment) {
        for (fileEnvironment in fileEnvironments) {
            val functionEnvironment = fileEnvironment.getFunction(id, args)
            if (functionEnvironment.isPresent) return functionEnvironment.get()
        }

        return null
    }

    return getParent()?.getFunction(id, args)
}

/**
 * Searches for function with given id and args in this environment and all parents
 *
 * @param id Function's id
 * @param parameters Function's parameters
 * @return Environment that has requested function or null
 */
fun Environment.getFunctionDeclarationEnvironment(id: String, parameters: List<DataType>): FunctionDeclarationEnvironment? {
    return getFunction(id, parameters)?.getParent()
}



fun Environment.getClass(classDesc: ClassDesc): ClassEnvironment? {
    if (classDesc.isPrimitive || classDesc.isArray) return null
    val globalEnvironment = getParentOrSelf<GlobalEnvironment>() ?: return null

    for (fileEnvironment in globalEnvironment.getFileEnvironments(classDesc.packageName())) {
        val classEnvironment = fileEnvironment.getClass(classDesc.displayName())
        if (classEnvironment.isPresent) return classEnvironment.get()
    }

    return globalEnvironment.resolveJavaClass(classDesc).orElse(null)
}

fun Environment.getClass(id: String): ClassEnvironment? {
    return getClass(ClassDesc.of(id))
}

fun Environment.getClassDeclarationEnvironment(classDesc: ClassDesc): ClassDeclarationEnvironment? {
    return getClass(classDesc)?.getParent()
}

fun Environment.getClassDeclarationEnvironment(id: String): ClassDeclarationEnvironment? {
    return getClassDeclarationEnvironment(ClassDesc.of(id))
}