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

/**
 * Represents environment for classes
 */
sealed interface ClassEnvironment : GlobalVariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    override fun getParent(): ClassDeclarationEnvironment

    val fullClassName: String

    fun getFunctionRecursively(id: String, args: List<DataType>): FunctionSymbol? {
        getFunction(id, args)?.let { return@getFunctionRecursively it }
        asSymbol().baseClass?.environment?.getFunctionRecursively(id, args)?.let { return@getFunctionRecursively it }

        for (interfaceSymbol in asSymbol().interfaces) {
            interfaceSymbol.environment.getFunctionRecursively(id, args)?.let { return@getFunctionRecursively it }
        }

        return null
    }
}



private class ClassEnvironmentImpl(parent: ClassDeclarationEnvironment) :
    ClassEnvironment,
    FunctionDeclarationEnvironment by FunctionDeclarationEnvironment(parent),
    ConstructorDeclarationEnvironment by ConstructorDeclarationEnvironment(parent),
    GlobalVariableDeclarationEnvironmentImpl(parent) {
    override fun getParent() = super.getParent() as ClassDeclarationEnvironment

    override fun getVariable(id: String): GlobalVariableSymbol? {
        val variableValue = super<GlobalVariableDeclarationEnvironmentImpl>.getVariable(id)
        if (variableValue != null) return variableValue
        return asSymbol().baseClass?.environment?.getVariable(id)
    }

    override fun getFunction(id: String, args: List<DataType>): FunctionSymbol? {
        val function = super<FunctionDeclarationEnvironment>.getFunction(id, args)
        if (function != null) return function
        return asSymbol().baseClass?.environment?.getFunction(id, args)
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



/**
 * Creates class environment
 * @param parent Parent
 * @return New class environment
 */
fun ClassEnvironment(parent: ClassDeclarationEnvironment): ClassEnvironment = ClassEnvironmentImpl(parent)