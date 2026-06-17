package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.Commands
import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.lexer.*
import me.itzisonn_.meazy.parser.*
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.statement.ProgramParsingFunction
import me.itzisonn_.meazy.runtime.ClassLoaderWrapper
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import me.itzisonn_.meazy.text.Language
import me.itzisonn_.registry.RegistryIdentifier
import me.itzisonn_.registry.multiple_entry.SetRegistry
import java.io.File
import java.lang.constant.ClassDesc
import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility
import kotlin.reflect.typeOf

/**
 * All basic Registries
 */
object Registries {
    private var isInitialized = false

    /**
     * Registry for all Languages
     * 
     * @see Language
     */
    val LANGUAGES = LanguageRegistry()



    /**
     * Registry for all Modifiers
     */
    val MODIFIERS = SetRegistry<Modifier>()

    /**
     * Registry for all Operators
     */
    val OPERATORS = OperatorRegistry()

    /**
     * Registry for function that parses tokens into [Program]
     * 
     * @see ParsingFunction
     * @see Parser
     */
    lateinit var parseTokensFunction: (File?, List<Token>) -> Program
        private set



    /**
     * Registry for function that compiles [Program] to bytecode
     */
    lateinit var compileProgramFunction: (Program) -> Map<ClassDesc, ByteArray>
        private set

    /**
     * Registry for function that runs [Program]
     */
    lateinit var runProgramFunction: (Map<ClassDesc, ByteArray>) -> Unit
        private set



    /**
     * Initializes Registries
     * 
     * 
     * *Don't use this method because it's called once at [MeazyMain] initialization*
     * 
     * @throws IllegalStateException If Registries has already been initialized
     */
    fun initialize() {
        check(!isInitialized) { "Registries have already been initialized" }
        isInitialized = true

        LANGUAGES.register(defaultIdentifier("english"), Language("en", "English"))
        LANGUAGES.register(defaultIdentifier("russian"), Language("ru", "Русский"))

        Commands.initialize()
        TokenTypes.initialize()
        Modifiers.register()
        Operators.register()

        parseTokensFunction = { file, tokens ->
            val parser = Parser(tokens)
            parser.parse(ProgramParsingFunction, file)
        }

        compileProgramFunction = { program ->
            val globalEnvironment = GlobalEnvironment()
            val bytecodeBuilders = BytecodeBuilders.of(null, null)
            val instructionsSet = InstructionsSet(bytecodeBuilders)

            program.declare(globalEnvironment)
            program.resolve(globalEnvironment)
            program.emit(instructionsSet, globalEnvironment)

            for (instruction in instructionsSet.instructions) {
                instruction.emit(bytecodeBuilders)
            }
            bytecodeBuilders.classes
        }

        runProgramFunction = { classes ->
            val classLoader = ClassLoaderWrapper()
            val mainMethods = mutableSetOf<KCallable<*>>()

            for ((classDesc, classFile) in classes) {
                val loadedClass = classLoader.defineClass(classFile).kotlin

                val method = loadedClass.members.find { it.name == "main" }
                if (method == null) {
                    System.err.println("No method main in class $classDesc")
                    continue
                }

                if (method.returnType != typeOf<Unit>() || method.parameters.isNotEmpty()) {
                    System.err.println("Main method has invalid signature in class $classDesc") //TODO
                    continue
                }

                if (method.visibility != KVisibility.PUBLIC) {
                    System.err.println("Main method is inaccessible in class $classDesc")
                    continue
                }

                mainMethods += method
            }

            mainMethods.forEach { it.call() }
        }
    }
}

/**
 * Creates new RegistryIdentifier with 'meazy' namespace
 *
 * @param id Identifier's id that matches [RegistryIdentifier.IDENTIFIER_REGEX]
 * @return New RegistryIdentifier
 *
 * @throws IllegalArgumentException If id doesn't match [RegistryIdentifier.IDENTIFIER_REGEX]
 */
fun defaultIdentifier(id: String): RegistryIdentifier =
    RegistryIdentifier.of("meazy", id)