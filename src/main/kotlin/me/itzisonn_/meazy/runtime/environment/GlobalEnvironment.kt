package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import java.lang.constant.ClassDesc

/**
 * Represents global environment
 */
sealed interface GlobalEnvironment : Environment {
    /**
     * Adds to this global environment file environment
     * @param fileEnvironment FileEnvironment to add
     */
    fun addFileEnvironment(fileEnvironment: FileEnvironment)

    fun getFileEnvironments(packageName: String): Set<FileEnvironment> {
        val fileEnvironments = mutableSetOf<FileEnvironment>()

        for (fileEnvironment in this.fileEnvironments) {
            if (packageName == fileEnvironment.packageName) fileEnvironments.add(fileEnvironment)
        }

        return fileEnvironments
    }

    /**
     * @return All file environments
     */
    val fileEnvironments: Set<FileEnvironment>


    fun resolveJavaClass(classDesc: ClassDesc): ClassEnvironment?
}



private class GlobalEnvironmentImpl : GlobalEnvironment {
    private val _fileEnvironments = mutableSetOf<FileEnvironment>()

    override fun addFileEnvironment(fileEnvironment: FileEnvironment) {
        _fileEnvironments.add(fileEnvironment)
    }

    override fun getParent() = null
    override val isShared = false
    override val fileEnvironments get() = _fileEnvironments.toSet()



    override fun resolveJavaClass(classDesc: ClassDesc): ClassEnvironment? {
        if (classDesc.isPrimitive || classDesc.isArray) return null

        try {
            val cls = Class.forName(classDesc.packageName() + "." + classDesc.displayName())
            val packageName = cls.getPackageName()

            for (fileEnvironment in getFileEnvironments(packageName)) {
                val classEnvironment = fileEnvironment.getClass(classDesc.displayName())
                if (classEnvironment != null) return classEnvironment
            }

            val fileEnvironment = FileEnvironment(
                this, packageName, cls.getSimpleName()
            )

            val classEnvironmentModifiers = mutableSetOf<Modifier>()
            if (!java.lang.reflect.Modifier.isFinal(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.open)
            if (java.lang.reflect.Modifier.isPrivate(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.private)
            if (java.lang.reflect.Modifier.isAbstract(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.abstract)

            val classEnvironment = ClassEnvironment(
                fileEnvironment,
                false,
                cls.isInterface,
                classDesc.displayName(),
                if (cls.getSuperclass() == null) null else cls.getSuperclass().describeConstable().orElseThrow(),
                cls.interfaces.map { c -> c.describeConstable().orElseThrow() }.toSet(),
                classEnvironmentModifiers
            )

            fileEnvironment.declareClass(classEnvironment)
            addFileEnvironment(fileEnvironment)

            if (classEnvironment.baseClass != null) resolveJavaClass(classEnvironment.baseClass!!)
            for (interfaceClassDesc in classEnvironment.interfaces) {
                resolveJavaClass(interfaceClassDesc)
            }

            for (method in cls.declaredMethods) {
                if (method.isSynthetic) continue

                val returnDataType: DataType?
                if (method.returnType == Void.TYPE) returnDataType = null
                else {
                    val returnTypeDesc = method.returnType.describeConstable().orElseThrow()
                    resolveJavaClass(returnTypeDesc)
                    returnDataType = DataType.of(returnTypeDesc, !method.returnType.isPrimitive)
                }

                val functionModifiers = mutableSetOf<Modifier>()
                if (!java.lang.reflect.Modifier.isFinal(method.modifiers)) functionModifiers.add(Modifiers.open)
                if (java.lang.reflect.Modifier.isPrivate(method.modifiers)) functionModifiers.add(Modifiers.private)
                if (java.lang.reflect.Modifier.isProtected(method.modifiers)) functionModifiers.add(Modifiers.protected)
                if (java.lang.reflect.Modifier.isStatic(method.modifiers)) functionModifiers.add(Modifiers.shared)
                if (java.lang.reflect.Modifier.isAbstract(method.modifiers)) functionModifiers.add(Modifiers.abstract)

                classEnvironment.declareFunction(
                    FunctionEnvironment(
                        classEnvironment,
                        null,
                        null,
                        method.name,
                        method.parameters.map { p ->
                            Parameter(
                                p.name,
                                DataType.of(
                                    p.getType().describeConstable().orElseThrow(),
                                    !p.getType().isPrimitive
                                ),
                                java.lang.reflect.Modifier.isFinal(p.modifiers)
                            )
                        },
                        returnDataType,
                        java.lang.reflect.Modifier.isStatic(method.modifiers),
                        functionModifiers
                    )
                )
            }

            for (constructor in cls.declaredConstructors) {
                val constructorModifiers = mutableSetOf<Modifier>()
                if (java.lang.reflect.Modifier.isPrivate(constructor.modifiers)) constructorModifiers.add(Modifiers.private)
                if (java.lang.reflect.Modifier.isProtected(constructor.modifiers)) constructorModifiers.add(Modifiers.protected)

                classEnvironment.declareConstructor(
                    ConstructorEnvironment(
                        classEnvironment,
                        null,
                        null,
                        constructorModifiers,
                        constructor.parameters
                            .map { p ->
                                Parameter(
                                    p.name,
                                    DataType.of(
                                        p.getType().describeConstable().orElseThrow(),
                                        !p.getType().isPrimitive
                                    ),
                                    java.lang.reflect.Modifier.isFinal(p.modifiers)
                                )
                            }.toList()
                    )
                )
            }

            for (field in cls.declaredFields) {
                val isNullable = if (field.type.isPrimitive) false
                else if (java.lang.reflect.Modifier.isFinal(field.modifiers) && java.lang.reflect.Modifier.isStatic(field.modifiers)) {
                    if (field.trySetAccessible()) field.get(null) == null
                    else true
                }
                else true

                val variableModifiers = mutableSetOf<Modifier>()
                if (!java.lang.reflect.Modifier.isFinal(field.modifiers)) variableModifiers.add(Modifiers.open)
                if (java.lang.reflect.Modifier.isPrivate(field.modifiers)) variableModifiers.add(Modifiers.private)
                if (java.lang.reflect.Modifier.isProtected(field.modifiers)) variableModifiers.add(Modifiers.protected)
                if (java.lang.reflect.Modifier.isStatic(field.modifiers)) variableModifiers.add(Modifiers.shared)

                classEnvironment.declareVariable(
                    field.name,
                    DataType.of(field.type.describeConstable().orElseThrow(), isNullable),
                    java.lang.reflect.Modifier.isFinal(field.modifiers),
                    null,
                    variableModifiers
                )
            }

            return classEnvironment
        }
        catch (_: ClassNotFoundException) {
            return null
        }
        catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }
}



/**
 * Creates global environment
 * @return New global environment
 */
fun GlobalEnvironment(): GlobalEnvironment = GlobalEnvironmentImpl()