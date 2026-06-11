package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import java.lang.constant.ClassDesc
import java.util.Optional

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


    fun resolveJavaClass(classDesc: ClassDesc): Optional<ClassEnvironment>
}



private class GlobalEnvironmentImpl : GlobalEnvironment {
    private val _fileEnvironments = mutableSetOf<FileEnvironment>()

    override fun addFileEnvironment(fileEnvironment: FileEnvironment) {
        _fileEnvironments.add(fileEnvironment)
    }

    override val fileEnvironments get() = _fileEnvironments.toSet()

    override fun getParent() = null

    override val isShared = false


    override fun resolveJavaClass(classDesc: ClassDesc): Optional<ClassEnvironment> {
        if (classDesc.isPrimitive || classDesc.isArray) return Optional.empty<ClassEnvironment>()

        try {
            val cls = Class.forName(classDesc.packageName() + "." + classDesc.displayName())
            val packageName = cls.getPackageName()

            for (fileEnvironment in getFileEnvironments(packageName)) {
                val classEnvironment = fileEnvironment.getClass(classDesc.displayName())
                if (classEnvironment.isPresent) return classEnvironment
            }

            val fileEnvironment = FileEnvironment(
                this, packageName, cls.getSimpleName()
            )

            val classEnvironmentModifiers = mutableSetOf<Modifier>()
            if (!java.lang.reflect.Modifier.isFinal(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.OPEN())
            if (java.lang.reflect.Modifier.isPrivate(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.PRIVATE())
            if (java.lang.reflect.Modifier.isAbstract(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.ABSTRACT())

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
                if (!java.lang.reflect.Modifier.isFinal(cls.modifiers)) functionModifiers.add(Modifiers.OPEN())
                if (java.lang.reflect.Modifier.isPrivate(cls.modifiers)) functionModifiers.add(Modifiers.PRIVATE())
                if (java.lang.reflect.Modifier.isProtected(cls.modifiers)) functionModifiers.add(Modifiers.PROTECTED())
                if (java.lang.reflect.Modifier.isStatic(cls.modifiers)) functionModifiers.add(Modifiers.SHARED())
                if (java.lang.reflect.Modifier.isAbstract(cls.modifiers)) functionModifiers.add(Modifiers.ABSTRACT())

                classEnvironment.declareFunction(
                    FunctionEnvironment(
                        classEnvironment,
                        null,
                        null,
                        method.name,
                        method.parameters.map { p ->
                            ParameterExpression(
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
                if (java.lang.reflect.Modifier.isPrivate(cls.modifiers)) constructorModifiers.add(Modifiers.PRIVATE())
                if (java.lang.reflect.Modifier.isProtected(cls.modifiers)) constructorModifiers.add(Modifiers.PROTECTED())

                classEnvironment.declareConstructor(
                    ConstructorEnvironment(
                        classEnvironment,
                        null,
                        null,
                        constructorModifiers,
                        constructor.parameters
                            .map { p ->
                                ParameterExpression(
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

                classEnvironment.declareVariable(
                    field.name,
                    DataType.of(field.type.describeConstable().orElseThrow(), isNullable),
                    java.lang.reflect.Modifier.isFinal(field.modifiers),
                    null
                )
            }

            return Optional.of(classEnvironment)
        }
        catch (_: ClassNotFoundException) {
            return Optional.empty()
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