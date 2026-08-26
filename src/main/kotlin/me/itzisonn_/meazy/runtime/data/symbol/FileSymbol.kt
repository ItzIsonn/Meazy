package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment

/**
 * Represents class symbol
 */
sealed interface FileSymbol : Symbol {
    val packageName: String
    val className: String
    val environment: FileEnvironment
}



private data class FileSymbolImpl(
    override val packageName: String,
    override val className: String,
    override val environment: FileEnvironment
) : FileSymbol

fun FileSymbol(
    packageName: String, className: String, environment: FileEnvironment
): FileSymbol = FileSymbolImpl(packageName, className, environment)