package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.runtime.VariableValue
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.util.Optional
import kotlin.reflect.KClass
import kotlin.reflect.cast

object EnvironmentUtils {
    /**
     * Searches for given environment as a parent in this environment and all parents
     * @param environment Environment to lookup
     * @return Whether this environment has requested parent
     */
    @JvmStatic
    fun hasParent(environment: Environment, target: Environment): Boolean {
        val parent = environment.getParent()
        return target == parent || (parent != null && hasParent(parent, target))
    }

    /**
     * Searches for environment that matches given predicate in all parents of given environment
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     */
    @JvmStatic
    fun hasParent(environment: Environment, predicate: (Environment?) -> Boolean): Boolean {
        val parent = environment.getParent()
        return predicate(parent) || parent != null && hasParent(parent, predicate)
    }

    /**
     * Searches for environment that matches given predicate in given environment and all of its parents
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     */
    fun hasParentOrSelf(environment: Environment, predicate: (Environment?) -> Boolean): Boolean {
        if (predicate(environment)) return true
        return hasParent(environment, predicate)
    }

    /**
     * Searches for environment that is instance of given class in all parents of given environment
     * @param cls Class of parent environment
     * @return Whether this environment has parent of given class
     */
    fun <T : Environment> hasParent(environment: Environment, cls: KClass<T>): Boolean {
        val parent = environment.getParent()
        if (cls.isInstance(parent)) return true
        if (parent != null) return hasParent(parent, cls)
        return false
    }

    /**
     * Searches for environment that is instance of given class in given environment and all of its parents
     * @param cls Class of parent environment
     * @return Whether this environment or its parent is instance of given class
     */
    fun <T : Environment> hasParentOrSelf(environment: Environment, cls: KClass<T>): Boolean {
        if (cls.isInstance(environment)) return true
        return hasParent(environment, cls)
    }

    @JvmStatic
    @Deprecated("Use function with KClass instead")
    fun <T : Environment> hasParentOrSelf(environment: Environment, cls: Class<T>): Boolean {
        return hasParentOrSelf(environment, cls.kotlin)
    }



    /**
     * Searches for environment that matches given predicate in all parents of given environment
     * @param predicate Predicate that matches parent environment
     * @return Parent that matches given predicate or null
     */
    fun getParent(environment: Environment, predicate: (Environment?) -> Boolean): Optional<Environment> {
        val parent = environment.getParent()
        if (predicate(parent)) return Optional.ofNullable(parent)
        if (parent != null) return getParent(parent, predicate)
        return Optional.empty()
    }

    /**
     * Searches for environment that matches given predicate in given environment and all of its parents
     * @param predicate Predicate that matches parent environment
     * @return Parent or given environment that matches given predicate or null
     */
    fun getParentOrSelf(environment: Environment, predicate: (Environment?) -> Boolean): Optional<Environment> {
        if (predicate(environment)) return Optional.of(environment)
        return getParent(environment, predicate)
    }

    /**
     * Searches for environment that is instance of given class in all parents of given environment
     * @param cls Class of parent environment
     * @return Parent of given class or null
     */
    fun <T : Environment> getParent(environment: Environment, cls: KClass<T>): Optional<T> {
        val parent = environment.getParent() ?: return Optional.empty()
        if (cls.isInstance(parent)) return Optional.of(cls.cast(parent))
        return getParent(parent, cls)
    }

    @JvmStatic
    @Deprecated("Use function with KClass instead")
    fun <T : Environment> getParent(environment: Environment, cls: Class<T>): Optional<T> {
        return getParent(environment, cls.kotlin)
    }

    /**
     * Searches for environment that is instance of given class in given environment and all of its parents
     * @param cls Class of parent environment
     * @return Parent or given environment of given class or null
     */
    fun <T : Environment> getParentOrSelf(environment: Environment, cls: KClass<T>): Optional<T> {
        if (cls.isInstance(environment)) return Optional.of((cls.cast(environment)))
        return getParent(environment, cls)
    }

    @JvmStatic
    @Deprecated("Use function with KClass instead")
    fun <T : Environment> getParentOrSelf(environment: Environment, cls: Class<T>): Optional<T> {
        return getParentOrSelf(environment, cls.kotlin)
    }



    /**
     * TODO
     */
    @JvmStatic
    fun isInstanceOf(environment: Environment, classDesc: ClassDesc, target: ClassDesc): Boolean {
        if (classDesc == target) return true

        val classEnvironment = getClassEnvironment(environment, classDesc).orElse(null)
            ?: return false

        val baseClass = classEnvironment.baseClass
        if (baseClass != null && isInstanceOf(environment, baseClass, target)) {
            return true
        }

        for (interfaceClassDesc in classEnvironment.interfaces) {
            if (isInstanceOf(environment, interfaceClassDesc, target)) return true
        }

        return false
    }

    /**
     * TODO
     */
    @JvmStatic
    fun getCommonOf(environment: Environment, classDesc1: ClassDesc, classDesc2: ClassDesc): ClassDesc? {
        if (classDesc1 == classDesc2) return classDesc1
        if (isInstanceOf(environment, classDesc1, classDesc2)) return classDesc2
        if (isInstanceOf(environment, classDesc2, classDesc1)) return classDesc1

        val classEnvironment = getClassEnvironment(environment, classDesc1).orElse(null)
            ?: return null

        var gotObjectAsCommon = false

        val baseClass = classEnvironment.baseClass
        if (baseClass != null) {
            val commonClassDesc = getCommonOf(environment, baseClass, classDesc2)
            if (ConstantDescs.CD_Object == commonClassDesc) gotObjectAsCommon = true
            else if (commonClassDesc != null) return commonClassDesc
        }

        for (interfaceClassDesc in classEnvironment.interfaces) {
            val commonClassDesc = getCommonOf(environment, interfaceClassDesc, classDesc2)
            if (commonClassDesc != null) return commonClassDesc
        }

        if (gotObjectAsCommon) return ConstantDescs.CD_Object
        return null
    }

    /**
     * TODO
     */
    @JvmStatic
    fun resolveClassDesc(environment: Environment, classDesc: ClassDesc, allowPrimitives: Boolean): ClassDesc {
        if (classDesc.isPrimitive || classDesc.isArray) return classDesc
        if (!classDesc.packageName().isEmpty()) return classDesc

        val fileEnvironment = getParentOrSelf(environment, FileEnvironment::class).orElse(null)
            ?: return classDesc

        var classEnvironment = fileEnvironment.getClass(classDesc.displayName()).orElse(null)
        if (classEnvironment != null) return classEnvironment.classDesc

        classEnvironment = getClassEnvironment(environment, classDesc).orElse(null)
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

        classEnvironment = getClassEnvironment(environment, fullId).orElse(null)
        if (classEnvironment != null) return classEnvironment.classDesc

        val resolvedClassDesc = ClassDesc.of(fullId)
        fileEnvironment.getParent().resolveJavaClass(resolvedClassDesc)
        return resolvedClassDesc
    }

    /**
     * TODO
     */
    @JvmStatic
    fun resolveClassDesc(environment: Environment, classDesc: String, allowPrimitives: Boolean): ClassDesc {
        return resolveClassDesc(environment, ClassDesc.of(classDesc), allowPrimitives)
    }



    /**
     * @return File environment in parent environments of given environment
     */
    fun getFileEnvironment(environment: Environment): Optional<FileEnvironment> {
        if (environment is FileEnvironment) return Optional.of(environment)
        return getParent(environment, FileEnvironment::class)
    }

    fun getClassName(environment: Environment): Optional<String> {
        val parent = getFileEnvironment(environment).orElse(null) ?: return Optional.empty()
        return Optional.of(parent.className)
    }

    /**
     * @return Package name of this environment
     */
    fun getPackageName(environment: Environment): Optional<String> {
        val parent = getFileEnvironment(environment).orElse(null) ?: return Optional.empty()
        return Optional.of(parent.packageName)
    }

    /**
     * @return Are two given environments from same package
     */
    @JvmStatic
    fun areFromSamePackage(environment1: Environment, environment2: Environment): Boolean {
        return getPackageName(environment1) == getPackageName(environment2)
    }



    /**
     * Searches for variable with given id in this environment and all parents
     * 
     * @param id Variable's id
     * @return Environment that has requested variable or null
     */
    fun getVariableDeclarationEnvironment(environment: Environment, id: String): Optional<VariableDeclarationEnvironment> {
        return getVariableValue(environment, id).map(VariableValue::parentEnvironment)
    }

    /**
     * Searches for variable with given id in this environment and all parents
     * 
     * @param id Variable's id
     * @return Environment that has requested variable or null
     */
    @JvmStatic
    fun getVariableValue(environment: Environment, id: String): Optional<VariableValue> {
        if (environment is VariableDeclarationEnvironment) {
            val variableValue = environment.getVariable(id)
            if (variableValue.isPresent) return variableValue
        }

        if (environment is GlobalEnvironment) {
            for (fileEnvironment in environment.fileEnvironments) {
                val variableValue = fileEnvironment.getVariable(id)
                if (variableValue.isPresent) return variableValue
            }

            return Optional.empty()
        }

        val parent = environment.getParent() ?: return Optional.empty()
        return getVariableValue(parent, id)
    }


    /**
     * Searches for function with given id and args in this environment and all parents
     * 
     * @param id Function's id
     * @param parameters Function's parameters
     * @return Environment that has requested function or null
     */
    fun getFunctionDeclarationEnvironment(environment: Environment, id: String, parameters: List<DataType>): Optional<FunctionDeclarationEnvironment> {
        return getFunctionEnvironment(environment, id, parameters).map { it.getParent() }
    }

    /**
     * Searches for function with given id and args in this environment and all parents
     * 
     * @param id Function's id
     * @param args Function's args
     * @return Environment that has requested function or null
     */
    @JvmStatic
    fun getFunctionEnvironment(environment: Environment, id: String, args: List<DataType>): Optional<FunctionEnvironment> {
        if (environment is FunctionDeclarationEnvironment) {
            val functionEnvironment = environment.getFunction(id, args)
            if (functionEnvironment.isPresent) return functionEnvironment
        }

        EnvironmentUtils::class.java.kotlin

        if (environment is GlobalEnvironment) {
            for (fileEnvironment in environment.fileEnvironments) {
                val functionEnvironment = fileEnvironment.getFunction(id, args)
                if (functionEnvironment.isPresent) return functionEnvironment
            }

            return Optional.empty()
        }

        val parent = environment.getParent() ?: return Optional.empty()
        return getFunctionEnvironment(parent, id, args)
    }


    fun getClassDeclarationEnvironment(environment: Environment, id: String): Optional<ClassDeclarationEnvironment> {
        return getClassDeclarationEnvironment(environment, ClassDesc.of(id))
    }

    fun getClassDeclarationEnvironment(environment: Environment, classDesc: ClassDesc): Optional<ClassDeclarationEnvironment> {
        return getClassEnvironment(environment, classDesc).map { it.getParent() }
    }

    @JvmStatic
    fun getClassEnvironment(environment: Environment, id: String): Optional<ClassEnvironment> {
        return getClassEnvironment(environment, ClassDesc.of(id))
    }

    @JvmStatic
    fun getClassEnvironment(environment: Environment, classDesc: ClassDesc): Optional<ClassEnvironment> {
        if (classDesc.isPrimitive || classDesc.isArray) return Optional.empty()

        val globalEnvironment = getParentOrSelf(environment, GlobalEnvironment::class).orElse(null)
            ?: return Optional.empty()

        for (fileEnvironment in globalEnvironment.getFileEnvironments(classDesc.packageName())) {
            val classEnvironment = fileEnvironment.getClass(classDesc.displayName())
            if (classEnvironment.isPresent) return classEnvironment
        }

        return globalEnvironment.resolveJavaClass(classDesc)
    }
}
