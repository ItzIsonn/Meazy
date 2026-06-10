package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.command.Commands
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.lexer.*
import me.itzisonn_.meazy.parser.*
import me.itzisonn_.meazy.parser.ast.program.Program
import me.itzisonn_.meazy.parser.ast.program.ProgramFactory
import me.itzisonn_.meazy.parser.ast.program.impl.ProgramFactoryImpl
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunctions
import me.itzisonn_.meazy.registry.Registries.PARSING_FUNCTIONS
import me.itzisonn_.meazy.registry.Registries.TOKEN_TYPES
import me.itzisonn_.meazy.runtime.ClassLoaderWrapper
import me.itzisonn_.meazy.runtime.environment.factory.*
import me.itzisonn_.meazy.runtime.environment.factory.impl.*
import me.itzisonn_.meazy.text.Language
import me.itzisonn_.registry.multiple_entry.OrderedRegistry
import me.itzisonn_.registry.multiple_entry.SetRegistry
import me.itzisonn_.registry.single_entry.SingleEntryRegistryImpl
import java.io.File
import java.lang.constant.ClassDesc
import java.lang.reflect.InvocationTargetException

/**
 * All basic Registries
 */
object Registries {
    private var isInit = false

    /**
     * Registry for all Languages
     * 
     * @see Language
     */
    val LANGUAGES = LanguageRegistry()



    /**
     * Registry for all Commands
     * 
     * @see AbstractCommand
     */
    val COMMANDS = CommandRegistry()



    /**
     * Registry for all TokenTypes
     */
    val TOKEN_TYPES = SetRegistry<TokenType>()

    /**
     * Registry for all TokenTypesSets
     */
    val TOKEN_TYPE_SETS = SetRegistry<TokenTypeSet>()

    /**
     * Registry for tokenization function that is used to tokenize given string
     * 
     * @see Token
     * @see TOKEN_TYPES
     */
    val TOKENIZATION_FUNCTION = SingleEntryRegistryImpl<(String) -> List<Token>>()



    /**
     * Registry for all Modifiers
     */
    @JvmField
    val MODIFIERS = SetRegistry<Modifier>()

    /**
     * Registry for all Operators
     */
    @JvmField
    val OPERATORS = OperatorRegistry()

    /**
     * Registry for all ParsingFunctions
     * 
     * @see ParsingFunction
     * @see Parser
     */
    @JvmField
    val PARSING_FUNCTIONS = OrderedRegistry<ParsingFunction<*>>()

    /**
     * Registry for function that uses [PARSING_FUNCTIONS] to parse tokens into [Program]
     * 
     * @see ParsingFunction
     * @see Parser
     */
    val PARSE_TOKENS_FUNCTION = SingleEntryRegistryImpl<(File?, List<Token>) -> Program>()

    /**
     * Registry for [ProgramFactory]
     */
    @JvmField
    val PROGRAM_FACTORY = SingleEntryRegistryImpl<ProgramFactory>()



    /**
     * Registry for function that compiles [Program] to bytecode
     */
    val COMPILE_PROGRAM_FUNCTION = SingleEntryRegistryImpl<(Program) -> Map<ClassDesc, ByteArray>>()

    /**
     * Registry for function that runs [Program]
     */
    val RUN_PROGRAM_FUNCTION = SingleEntryRegistryImpl<(Map<ClassDesc, ByteArray>) -> Unit>()



    /**
     * Registry for [GlobalEnvironmentFactory]
     */
    val GLOBAL_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<GlobalEnvironmentFactory>()

    /**
     * Registry for [FileEnvironmentFactory]
     */
    @JvmField
    val FILE_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<FileEnvironmentFactory>()

    /**
     * Registry for [ClassEnvironmentFactory]
     */
    @JvmField
    val CLASS_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<ClassEnvironmentFactory>()

    /**
     * Registry for [FunctionEnvironmentFactory]
     */
    @JvmField
    val FUNCTION_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<FunctionEnvironmentFactory>()

    /**
     * Registry for [ConstructorEnvironmentFactory]
     */
    @JvmField
    val CONSTRUCTOR_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<ConstructorEnvironmentFactory>()

    /**
     * Registry for [LoopEnvironmentFactory]
     */
    @JvmField
    val LOOP_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<LoopEnvironmentFactory>()

    /**
     * Registry for [LocalVariableDeclarationEnvironmentFactory]
     */
    @JvmField
    val LOCAL_VARIABLE_DECLARATION_ENVIRONMENT_FACTORY = SingleEntryRegistryImpl<LocalVariableDeclarationEnvironmentFactory>()



    /**
     * Initializes Registries
     * 
     * 
     * *Don't use this method because it's called once at [MeazyMain] initialization*
     * 
     * @throws IllegalStateException If Registries has already been initialized
     */
    fun init() {
        check(!isInit) { "Registries have already been initialized" }
        isInit = true

        LANGUAGES.register(getDefaultIdentifier("english"), Language("en", "English"))
        LANGUAGES.register(getDefaultIdentifier("russian"), Language("ru", "Русский"))

        Commands.register()
        TokenTypes.register()
        Modifiers.REGISTER()
        Operators.REGISTER()
        ParsingFunctions.REGISTER()

        TOKENIZATION_FUNCTION.register(getDefaultIdentifier("tokens_function")) { lines ->
            val tokens = mutableListOf<Token>()
            var lineNumber = 1

            var i = 0
            while (i < lines.length) {
                val string = lines.substring(i)
                var token: Token? = null

                for (entry in TOKEN_TYPES.getEntries()) {
                    val tokenType: TokenType = entry.getValue()!!
                    if (tokenType.pattern == null) continue

                    val matcher = tokenType.pattern.matcher(string)
                    if (matcher.find()) {
                        val end = matcher.end()
                        val matched = string.substring(0, end)
                        if (!tokenType.canMatch.invoke(matched)) continue

                        if (token == null || token.value.length < matched.length) {
                            token = Token(lineNumber, i, end, tokenType, matched)
                        }
                    }
                }

                if (token == null) {
                    var errorString = string.split("\n".toRegex()).dropLastWhile { it.isEmpty() }[0]
                    if (errorString.length > 20) errorString = errorString.substring(0, 20) + "..."

                    throw UnknownTokenException(lineNumber, errorString)
                }

                i += token.value.length - 1
                if (!token.type.shouldSkip) tokens.add(token)

                lineNumber += token.value.length - token.value.replace("\n", "").length
                i++
            }

            tokens.add(Token(lineNumber, lines.length, lines.length, TokenTypes.endOfFile, ""))
            tokens
        }

        PARSE_TOKENS_FUNCTION.register(getDefaultIdentifier("parse_tokens")) { file, tokens ->
            val parsingContext = ParsingContext(tokens)
            val parser = parsingContext.parser

            parser.parse(getDefaultIdentifier("program"), Program::class.java, file)
        }

        COMPILE_PROGRAM_FUNCTION.register(getDefaultIdentifier("compile_program")) { program ->
            val globalEnvironment = GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue()!!.create()
            val bytecodeBuilders = BytecodeBuilders.of(null, null)
            val instructionsSet = InstructionsSet(bytecodeBuilders)

            program.declare(globalEnvironment)
            program.resolve(globalEnvironment)
            program.emit(instructionsSet, globalEnvironment, null)

            for (instruction in instructionsSet.instructions) {
                instruction.emit(bytecodeBuilders)
            }
            bytecodeBuilders.getClasses()
        }

        RUN_PROGRAM_FUNCTION.register(getDefaultIdentifier("run_program")) { classes ->
            val classLoader = ClassLoaderWrapper()
            for (classDesc in classes.keys) {
                val classFile: ByteArray = classes[classDesc]!!

                val loadedClass = classLoader.defineClass(classFile)
                try {
                    val method = loadedClass.getDeclaredMethod("main")

                    if (method.returnType != Void.TYPE || method.parameters.size != 0) {
                        System.err.println("Main method has invalid signature in class$classDesc") //TODO
                        continue
                    }

                    if (!method.canAccess(null)) {
                        System.err.println("Main method is inaccessible in class $classDesc")
                        continue
                    }

                    method.invoke(null)
                }
                catch (`_`: NoSuchMethodException) {
                    System.err.println("No method main in class $classDesc")
                }
                catch (e: IllegalAccessException) {
                    throw RuntimeException(e)
                }
                catch (e: InvocationTargetException) {
                    throw RuntimeException(e)
                }
            }
        }

        PROGRAM_FACTORY.register(getDefaultIdentifier("program_factory"), ProgramFactoryImpl())
        GLOBAL_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("global_environment_factory"),
            GlobalEnvironmentFactoryImpl()
        )
        FILE_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("file_environment_factory"),
            FileEnvironmentFactoryImpl()
        )
        CLASS_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("class_environment_factory"),
            ClassEnvironmentFactoryImpl()
        )
        FUNCTION_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("function_environment_factory"),
            FunctionEnvironmentFactoryImpl()
        )
        CONSTRUCTOR_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("constructor_environment_factory"),
            ConstructorEnvironmentFactoryImpl()
        )
        LOOP_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("loop_environment_factory"),
            LoopEnvironmentFactoryImpl()
        )
        LOCAL_VARIABLE_DECLARATION_ENVIRONMENT_FACTORY.register(
            getDefaultIdentifier("local_variable_declaration_environment_factory"),
            LocalVariableDeclarationEnvironmentFactoryImpl()
        )
    }
}