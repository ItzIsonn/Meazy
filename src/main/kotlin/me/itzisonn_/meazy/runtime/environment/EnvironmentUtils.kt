package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.VariableDeclarationEnvironment
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
    return target == parent || parent != null && parent.hasParent(target)
}

/**
 * Searches for environment that matches given predicate in all parents of given environment
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment has requested parent
 */
fun Environment.hasParent(predicate: (Environment) -> Boolean): Boolean {
    val parent = getParent() ?: return false
    return predicate(parent) || parent.hasParent(predicate)
}

/**
 * Searches for environment that matches given predicate in given environment and all of its parents
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment has requested parent
 */
fun Environment.hasParentOrSelf(predicate: (Environment) -> Boolean) = predicate(this) || hasParent(predicate)

/**
 * Searches for environment that is instance of given class in all parents of given environment
 * @param cls Class of parent environment
 * @return Whether this environment has parent of given class
 */
fun <T : Environment> Environment.hasParent(cls: KClass<T>): Boolean = getParent().let { parent ->
    cls.isInstance(parent) || parent?.hasParent(cls) ?: false
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
fun <T : Environment> Environment.hasParentOrSelf(cls: KClass<T>) = cls.isInstance(this) || hasParent(cls)

/**
 * Searches for environment that is instance of given class in given environment and all of its parents
 * @param T Type of parent environment
 * @return Whether this environment or its parent is instance of given class
 */
inline fun <reified T : Environment> Environment.hasParentOrSelf() = hasParentOrSelf(T::class)

/**
 * Searches for environment that is instance of given class and matches given predicate in all parents of given environment
 * @param cls Class of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment has parent that is instance of given class and matches given predicate
 */
fun <T : Environment> Environment.hasParent(cls: KClass<T>, predicate: (T) -> Boolean): Boolean {
    val parent = getParent() ?: return false
    if (cls.isInstance(parent)) {
        val castedParent = cls.cast(parent)
        if (predicate(castedParent)) return true
    }
    return parent.hasParent(cls, predicate)
}

/**
 * Searches for environment that is instance of given class and matches given predicate in all parents of given environment
 * @param T Type of parent environment
 * @param predicate Predicate that matches parent environment
 * @return  Whether this environment has parent that is instance of given class and matches given predicate
 */
@JvmName("hasParentOfType")
inline fun <reified T : Environment> Environment.hasParent(noinline predicate: (T) -> Boolean) = hasParent(T::class, predicate)

/**
 * Searches for environment that is instance of given class and matches given predicate in given environment and all of its parents
 * @param cls Class of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment or its parent is instance of given class and matches given predicate
 */
fun <T : Environment> Environment.hasParentOrSelf(cls: KClass<T>, predicate: (T) -> Boolean): Boolean {
    return cls.isInstance(this) && predicate(cls.cast(this)) || hasParent(cls, predicate)
}

/**
 * Searches for environment that is instance of given class and matches given predicate in given environment and all of its parents
 * @param T Type of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Whether this environment or its parent is instance of given class and matches given predicate
 */
@JvmName("hasParentOrSelfOfType")
inline fun <reified T : Environment> Environment.hasParentOrSelf(noinline predicate: (T) -> Boolean) = hasParentOrSelf(T::class, predicate)



/**
 * Searches for environment that matches given predicate in all parents of given environment
 * @param predicate Predicate that matches parent environment
 * @return Parent that matches given predicate or null
 */
fun Environment.getParent(predicate: (Environment) -> Boolean): Environment? = getParent()?.let { parent ->
    if (predicate(parent)) parent
    else parent.getParent(predicate)
}

/**
 * Searches for environment that matches given predicate in given environment and all of its parents
 * @param predicate Predicate that matches parent environment
 * @return Parent or given environment that matches given predicate or null
 */
fun Environment.getParentOrSelf(predicate: (Environment) -> Boolean): Environment? {
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
 * Searches for environment that is instance of given class and matches given predicate in all parents of given environment
 * @param cls Class of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Parent of given class and that matches given predicate or null
 */
fun <T : Environment> Environment.getParent(cls: KClass<T>, predicate: (T) -> Boolean): T? {
    val parent = getParent() ?: return null
    if (cls.isInstance(parent)) {
        val castedParent = cls.cast(parent)
        if (predicate(castedParent)) return castedParent
    }
    return parent.getParent(cls, predicate)
}

/**
 * Searches for environment that is instance of given class and matches given predicate in all parents of given environment
 * @param T Type of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Parent of given class and that matches given predicate or null
 */
@JvmName("getParentOfType")
inline fun <reified T : Environment> Environment.getParent(noinline predicate: (T) -> Boolean) = getParent(T::class, predicate)

/**
 * Searches for environment that is instance of given class and matches given predicate in given environment and all of its parents
 * @param cls Class of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Parent or given environment of given class and that matches given predicate or null
 */
fun <T : Environment> Environment.getParentOrSelf(cls: KClass<T>, predicate: (T) -> Boolean): T? {
    if (cls.isInstance(this)) {
        val castedThis = cls.cast(this)
        if (predicate(castedThis)) return castedThis
    }
    return getParent(cls, predicate)
}

/**
 * Searches for environment that is instance of given class and matches given predicate in given environment and all of its parents
 * @param T Type of parent environment
 * @param predicate Predicate that matches parent environment
 * @return Parent or given environment of given class and that matches given predicate or null
 */
@JvmName("getParentOrSelfOfType")
inline fun <reified T : Environment> Environment.getParentOrSelf(noinline predicate: (T) -> Boolean) = getParentOrSelf(T::class, predicate)



/**
 * TODO
 */
fun Environment.isInstanceOf(classDesc: ClassDesc, target: ClassDesc): Boolean {
    if (classDesc == target) return true
    val classSymbol = getClass(classDesc) ?: return false

    val baseClass = classSymbol.baseClass
    if (baseClass != null && isInstanceOf(baseClass.classDesc, target)) {
        return true
    }

    for (interfaceSymbol in classSymbol.interfaces) {
        if (isInstanceOf(interfaceSymbol.classDesc, target)) return true
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

    val classSymbol = getClass(classDesc1) ?: return null
    var gotObjectAsCommon = false

    val baseClass = classSymbol.baseClass
    if (baseClass != null) {
        val commonClassDesc = getCommonOf(baseClass.classDesc, classDesc2)
        if (ConstantDescs.CD_Object == commonClassDesc) gotObjectAsCommon = true
        else if (commonClassDesc != null) return commonClassDesc
    }

    for (interfaceSymbol in classSymbol.interfaces) {
        val commonClassDesc = getCommonOf(interfaceSymbol.classDesc, classDesc2)
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
    if (classDesc.packageName().isNotEmpty()) return classDesc

    val fileEnvironment = getParentOrSelf<FileEnvironment>() ?: return classDesc
    fileEnvironment.getClass(classDesc.displayName())?.let { return@resolveClassDesc it.classDesc }

    fileEnvironment.imports[classDesc.displayName()]?.let { fullName ->
        getClass(fullName)?.let { return@resolveClassDesc it.classDesc }
    }

    if (allowPrimitives) {
        when (classDesc.displayName()) {
            "Int" -> return ConstantDescs.CD_int
            "Long" -> return ConstantDescs.CD_long
            "Float" -> return ConstantDescs.CD_float
            "Double" -> return ConstantDescs.CD_double
            "Boolean" -> return ConstantDescs.CD_boolean
        }
    }

    when (classDesc.displayName()) {
        "Any" -> return ConstantDescs.CD_Object
        "Int" -> return ConstantDescs.CD_Integer
        "Long" -> return ConstantDescs.CD_Long
        "Float" -> return ConstantDescs.CD_Float
        "Double" -> return ConstantDescs.CD_Double
        "Boolean" -> return ConstantDescs.CD_Boolean
        "String" -> return ConstantDescs.CD_String
        "System" -> return ClassDesc.of("java.lang.System")
        "IO" -> return ClassDesc.of("java.lang.IO")
    }

    return classDesc
}

/**
 * TODO
 */
fun Environment.resolveClass(id: String): ClassSymbol? {
    val fileEnvironment = getParentOrSelf<FileEnvironment>() ?: return null
    fileEnvironment.getClass(id)?.let { return@resolveClass it }

    fileEnvironment.imports[id]?.let { fullName ->
        getClass(fullName)?.let { return@resolveClass it }
    }

    when (id) {
        "Any" -> return getClass(ConstantDescs.CD_Object)
        "Int" -> return getClass(ConstantDescs.CD_Integer)
        "Long" -> return getClass(ConstantDescs.CD_Long)
        "Float" -> return getClass(ConstantDescs.CD_Float)
        "Double" -> return getClass(ConstantDescs.CD_Double)
        "Boolean" -> return getClass(ConstantDescs.CD_Boolean)
        "String" -> return getClass(ConstantDescs.CD_String)
        "System" -> return getClass(ClassDesc.of("java.lang.System"))
        "IO" -> return getClass(ClassDesc.of("java.lang.IO"))
    }

    return null
}

/**
 * TODO
 */
fun Environment.resolveClassDesc(classDesc: String, allowPrimitives: Boolean) = resolveClassDesc(ClassDesc.of(classDesc), allowPrimitives)



/**
 * @return File environment in parent environments of given environment
 */
fun Environment.getFileEnvironment() = getParentOrSelf<FileEnvironment>()

/**
 * @return Class name of this environment
 */
fun Environment.getClassName() = getFileEnvironment()?.className

/**
 * @return Package name of this environment
 */
fun Environment.getPackageName() = getFileEnvironment()?.packageName

/**
 * @return Whether two given environments are from same package
 */
fun areFromSamePackage(environment1: Environment, environment2: Environment) = environment1.getPackageName() == environment2.getPackageName()

fun Environment.getFullClassName(): String? {
    if (this is ClassEnvironment) return fullClassName
    if (this is FileEnvironment) return fullClassName
    return getParent()?.getFullClassName()
}



/**
 * Searches for variable with given id in this environment and all parents
 *
 * @param id Variable's id
 * @return Environment that has requested variable or null
 */
fun Environment.getVariable(id: String): VariableSymbol? {
    if (this is VariableDeclarationEnvironment<*>) {
        getVariable(id)?.let { return@getVariable it }
    }

    if (this is FileEnvironment) {
        imports[id]?.let { fullName ->
            val packageName = fullName.substringBeforeLast(".")

            for (fileEnvironment in getParent().getFileEnvironments(packageName)) {
                fileEnvironment.getVariable(id)?.let { return@getVariable it }
            }
        }
    }

    return getParent()?.getVariable(id)
}

/**
 * Searches for variable with given id in this environment and all parents
 *
 * @param id Variable's id
 * @return Environment that has requested variable or null
 */
fun Environment.getVariableDeclarationEnvironment(id: String) = getVariable(id)?.parentEnvironment



/**
 * Searches for function with given id and args in this environment and all parents
 *
 * @param id Function's id
 * @param args Function's args
 * @return Environment that has requested function or null
 */
fun Environment.getFunction(id: String, args: List<DataType>): FunctionSymbol? {
    if (this is FunctionDeclarationEnvironment) {
        getFunction(id, args)?.let { return@getFunction it }
    }

    if (this is FileEnvironment) {
        imports[id]?.let { fullName ->
            val packageName = fullName.substringBeforeLast(".")

            for (fileEnvironment in getParent().getFileEnvironments(packageName)) {
                fileEnvironment.getFunction(id, args)?.let { return@getFunction it }
            }
        }
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
fun Environment.getFunctionDeclarationEnvironment(id: String, parameters: List<DataType>) =
    getFunction(id, parameters)?.environment?.getParent()

fun FunctionEnvironment.asSymbol(): FunctionSymbol {
    return getParent().functions.find { it.environment == this } ?: error("Can't find FunctionSymbol associated with FunctionEnvironment $this")
}



fun Environment.getClass(classDesc: ClassDesc): ClassSymbol? {
    if (classDesc.isPrimitive || classDesc.isArray) return null
    val globalEnvironment = getParentOrSelf<GlobalEnvironment>() ?: return null

    for (fileEnvironment in globalEnvironment.getFileEnvironments(classDesc.packageName())) {
        val classSymbol = fileEnvironment.getClass(classDesc.displayName())
        if (classSymbol != null) return classSymbol
    }

    return globalEnvironment.resolveJavaClass(classDesc)
}

fun Environment.getClass(id: String) = getClass(ClassDesc.of(id))

fun Environment.getClassDeclarationEnvironment(classDesc: ClassDesc) = getClass(classDesc)?.environment?.getParent()

fun Environment.getClassDeclarationEnvironment(id: String) = getClassDeclarationEnvironment(ClassDesc.of(id))

fun ClassEnvironment.asSymbol(): ClassSymbol {
    return getParent().classes.find { it.environment == this } ?: error("Can't find ClassSymbol associated with ClassEnvironment $this")
}