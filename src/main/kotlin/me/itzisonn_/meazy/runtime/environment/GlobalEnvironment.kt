package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.data.symbol.ConstructorSymbol
import me.itzisonn_.meazy.runtime.data.symbol.FunctionSymbol
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


    fun resolveJavaClass(classDesc: ClassDesc): ClassSymbol?
}



typealias JavaModifier = java.lang.reflect.Modifier

private class GlobalEnvironmentImpl : GlobalEnvironment {
    private val _fileEnvironments = mutableSetOf<FileEnvironment>()

    override fun addFileEnvironment(fileEnvironment: FileEnvironment) {
        _fileEnvironments.add(fileEnvironment)
    }

    override fun getParent() = null
    override val isShared = false
    override val fileEnvironments get() = _fileEnvironments.toSet()

    override fun resolveJavaClass(classDesc: ClassDesc): ClassSymbol? {
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
            if (!JavaModifier.isFinal(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.open)
            if (JavaModifier.isPrivate(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.private)
            if (JavaModifier.isAbstract(cls.modifiers)) classEnvironmentModifiers.add(Modifiers.abstract)

            val classEnvironment = ClassEnvironment(
                fileEnvironment,
                false,
                cls.isInterface,
                classDesc.displayName(),
                if (cls.getSuperclass() == null) null else cls.getSuperclass().describeConstable().orElseThrow(),
                cls.interfaces.map { it.describeConstable().orElseThrow() }.toSet(),
                classEnvironmentModifiers
            )

            val classSymbol = ClassSymbol(
                classDesc.displayName(), cls.isInterface,
                cls.interfaces
                    .map { it.describeConstable().orElseThrow() }
                    .map { it.packageName() + "." + it.displayName() }
                    .toMutableSet().apply {
                        if (cls.getSuperclass() != null) {
                            val baseClass = cls.getSuperclass().describeConstable().orElseThrow()
                            add(baseClass.packageName() + "." + baseClass.displayName())
                        }
                    },
                classEnvironmentModifiers,
                classEnvironment
            )

            fileEnvironment.declareClass(classSymbol)
            addFileEnvironment(fileEnvironment)

            val baseClass = classEnvironment.baseClass
            if (baseClass != null) resolveJavaClass(baseClass)

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
                if (!JavaModifier.isFinal(method.modifiers)) functionModifiers.add(Modifiers.open)
                if (JavaModifier.isPrivate(method.modifiers)) functionModifiers.add(Modifiers.private)
                if (JavaModifier.isProtected(method.modifiers)) functionModifiers.add(Modifiers.protected)
                if (JavaModifier.isStatic(method.modifiers)) functionModifiers.add(Modifiers.shared)
                if (JavaModifier.isAbstract(method.modifiers)) functionModifiers.add(Modifiers.abstract)

                val functionEnvironment = FunctionEnvironment(
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
                            )
                        )
                    },
                    returnDataType,
                    JavaModifier.isStatic(method.modifiers),
                    functionModifiers
                )

                classEnvironment.declareFunction(
                    FunctionSymbol(
                        method.name,
                        method.parameters.map { p ->
                            Parameter(
                                p.name,
                                DataType.of(
                                    p.getType().describeConstable().orElseThrow(),
                                    !p.getType().isPrimitive
                                )
                            )
                        },
                        returnDataType,
                        functionModifiers,
                        functionEnvironment
                    )
                )
            }

            for (constructor in cls.declaredConstructors) {
                val constructorModifiers = mutableSetOf<Modifier>()
                if (JavaModifier.isPrivate(constructor.modifiers)) constructorModifiers.add(Modifiers.private)
                if (JavaModifier.isProtected(constructor.modifiers)) constructorModifiers.add(Modifiers.protected)

                val constructorEnvironment = ConstructorEnvironment(
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
                                )
                            )
                        }.toList()
                )

                classEnvironment.declareConstructor(
                    ConstructorSymbol(
                        constructor.parameters
                            .map { p ->
                                Parameter(
                                    p.name,
                                    DataType.of(
                                        p.getType().describeConstable().orElseThrow(),
                                        !p.getType().isPrimitive
                                    )
                                )
                            }.toList(),
                        constructorModifiers,
                        constructorEnvironment
                    )
                )
            }

            for (field in cls.declaredFields) {
                val isNullable = if (field.type.isPrimitive) false
                else if (JavaModifier.isFinal(field.modifiers) && JavaModifier.isStatic(field.modifiers)) {
                    if (field.trySetAccessible()) field.get(null) == null
                    else true
                }
                else true

                val variableModifiers = mutableSetOf<Modifier>()
                if (!JavaModifier.isFinal(field.modifiers)) variableModifiers.add(Modifiers.open)
                if (JavaModifier.isPrivate(field.modifiers)) variableModifiers.add(Modifiers.private)
                if (JavaModifier.isProtected(field.modifiers)) variableModifiers.add(Modifiers.protected)
                if (JavaModifier.isStatic(field.modifiers)) variableModifiers.add(Modifiers.shared)

                classEnvironment.declareVariable(
                    field.name,
                    DataType.of(field.type.describeConstable().orElseThrow(), isNullable),
                    JavaModifier.isFinal(field.modifiers),
                    null,
                    variableModifiers
                )
            }

            return classSymbol
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