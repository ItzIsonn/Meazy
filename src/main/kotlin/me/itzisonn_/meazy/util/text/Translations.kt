package me.itzisonn_.meazy.util.text

/**
 * Represents translations bundle
 */
object Translations {
    private val translations = mapOf(
        "file.doesnt_exist" to "File '{0}' doesn't exist",
        "file.already_exists" to "File '{0}' already exists",
        "file.failed_read" to "Failed to read file '{0}'",
        "file.created" to "Created file '{0}'",
        "file.cant_create" to "Can't create file '{0}'",
        "file.cant_create_parent" to "Can't create parent file of file '{0}'",
        "file.unsupported_extension" to "File '{0}' has unsupported extension '{0}'",
        "file.output_cant_be_directory" to "Output file can't be directory",



        "commands.unknown" to "Unknown command with id '{0}'",
        "commands.incorrect_args_amount" to "Incorrect number of arguments for command with id '{0}'",
        "commands.arg_doesnt_match" to "Given argument '{0}' at position {1} doesn't match any available arguments",
        "commands.available" to "Available commands:",
        "commands.initialization_time" to "Initialized in {0}s",

        "commands.version" to "Meazy version {0}",

        "commands.run.running" to "Running file '{0}'",
        "commands.run.incompatible_version" to "Can't run file that has been compiled by a more recent version of the Meazy ({0}) in an older version ({1})",
        "commands.run.unsafe" to "It's unsafe to run file that has been compiled by an older version of the Meazy ({0}) in a more recent version ({1})",
        "commands.run.info" to "Executed in {0}s",

        "commands.compile.compiling" to "Compiling file '{0}'",
        "commands.compile.info" to "Compiled in {0}s",



        "invalid_identifier" to "Invalid identifier '{0}'",
        "lexer.unknown_token" to "Unknown token at line {0}: {1}",



        "parser.unexpected_token" to "Unexpected token '{0}' at line {1}",
        "parser.expected" to "Expected '{0}'",
        "parser.expected.keyword" to "Expected '{0}' keyword",
        "parser.expected.statement" to "Expected '{0}' statement",
        "parser.expected.expression" to "Expected '{0}' expression",
        "parser.expected.after" to "Expected '{0}' after '{1}'",
        "parser.expected.after_keyword" to "Expected '{0}' after '{1}' keyword",
        "parser.expected.after_statement" to "Expected '{0}' after '{1}' statement",
        "parser.expected.start" to "Expected '{0}' at the start of the '{1}'",
        "parser.expected.start_statement" to "Expected '{0}' at the start of the '{1}' statement",
        "parser.expected.start_expression" to "Expected '{0}' at the start of the '{1}' expression",
        "parser.expected.end" to "Expected '{0}' at the end of the '{1}'",
        "parser.expected.end_statement" to "Expected '{0}' at the end of the '{1}' statement",
        "parser.expected.end_expression" to "Expected '{0}' at the end of the '{1}' expression",
        "parser.expected.separator" to "Expected '{0}' as a separator between parts of '{1}'",
        "parser.expected.separator_statement" to "Expected '{0}' as a separator between parts of '{1}' statement",
        "parser.expected.separator_expression" to "Expected '{0}' as a separator between parts of '{1}' expression",

        "parser.modifier.doesnt_exist" to "Modifier with id '{0}' doesn't exist",
        "parser.modifier.unexpected" to "Found unexpected modifier",

        "parser.exception.call_not_identifier" to "Can't use non-identifier in 'call' expression",
        "parser.exception.member_expression" to "Right side of 'member' expression must be either id or 'call' expression",
        "parser.exception.cant_parse" to "Can't parse token with type '{0}'",
        "parser.exception.enums.base_classes" to "Enum class can't have base classes",
        "parser.exception.enums.duplicated_entries" to "Enum class can't have duplicated entries",
        "parser.exception.foreach_variable_without_datatype" to "Can't declare variable without data type in 'foreach' statement",
        "parser.exception.parameter_without_datatype" to "Can't declare parameter without data type",
        "parser.exception.global_statement" to "Expected 'variable_declaration', 'function_declaration' or 'class_declaration' statement",
        "parser.exception.statement" to "Expected 'call', 'postfix' or 'member' expression",
        "parser.exception.constant_without_value" to "Can't declare a constant variable without a value",
        "parser.exception.variable_without_datatype_and_value" to "Can't declare a variable without both data type and value",
        "parser.exception.not_at_beginning" to "'{0}' statement must be at the beginning of the file",
        "parser.exception.invalid_statement" to "Invalid statement at line {0}: {1}",
        "parser.exception.invalid_syntax" to "Invalid syntax at line {0}: {1}",
        "parser.exception.string_quote_not_closed" to "String '{0}' has unclosed quote",



        "runtime.file_doesnt_contain_main_function" to "File doesn't contain main function",
        "runtime.number_too_big" to "Number {0} is too big",
        "runtime.cant_use_statement" to "Can't use '{0}' statement in this environment",
        "runtime.cant_use_modifier" to "Can't use '{0}' modifier",
        "runtime.cant_apply_foreach" to "Can't apply foreach to non-collection",
        "runtime.statement_must_be_last" to "'{0}' statement must be last in body",
        "runtime.cant_run_program" to "Can't run program in non-file environment",
        "runtime.cant_assign_value" to "Can't assign value to '{0}'",
        "runtime.condition_must_be_boolean" to "Condition must be boolean value",
        "runtime.value_out_of_bounds" to "Resulted value {0} is out of bounds",
        "runtime.cant_evaluate_with_operator" to "Can't evaluate expression with operator '{0}'",
        "runtime.cant_multiply.values" to "Can't multiply values '{0}' and '{1}'",
        "runtime.cant_multiply.string_by_negative" to "Can't multiply string by a negative int",
        "runtime.unknown_postfix_operator" to "Unknown postfix operator '{0}'",
        "runtime.unknown_call_identifier" to "Unknown call identifier '{0}'",

        "runtime.class.doesnt_exist" to "Class with id '{0}' doesn't exist",
        "runtime.class.already_exists" to "Class with id '{0}' already exists",
        "runtime.class.cant_access" to "Can't access class with id '{0}'",
        "runtime.class.cant_call_base" to "Can't call base class with id '{0}' because it's not the base class of class with id '{1}'",
        "runtime.class.cant_inherit" to "Can't inherit final class with id '{0}'",
        "runtime.class.cant_extend" to "Can't extend final class with id '{0}'",
        "runtime.class.repeated.base_classes" to "Class with id '{0}' has repeated base classes",
        "runtime.class.repeated.functions" to "Class with id '{0}' has repeated functions",
        "runtime.class.repeated.variables" to "Class with id '{0}' has repeated variables",
        "runtime.class.instance.not_class" to "Can't create new instance of '{0}' because it's not a class",
        "runtime.class.instance.abstract" to "Can't create instance of an abstract class with id '{0}'",
        "runtime.class.instance.enum" to "Can't create instance of an enum class with id '{0}'",

        "runtime.function.doesnt_exist" to "Function with id '{0}' doesn't exist",
        "runtime.function.already_exists" to "Function with id '{0}' already exists",
        "runtime.function.cant_access" to "Can't access function with id '{0}'",
        "runtime.function.cant_call" to "Can't call '{0}' because it's not a function",
        "runtime.function.cant_override" to "Can't override final function with id '{0}'",
        "runtime.function.parameters_dont_match" to "Can't call function with id '{0}': expected {1} parameters but found {2}",
        "runtime.function.abstract_not_initialized" to "Abstract function with id '{0}' in class with id '{1}' hasn't been initialized",
        "runtime.function.operator.already_exists" to "Function for operator with id '{0}' already exists",
        "runtime.function.operator.outside_class" to "Can't declare operator function with id '{0}' outside a class",
        "runtime.function.operator.doesnt_exist" to "Can't declare operator function because operator with id '{0}' doesn't exist",
        "runtime.function.operator.parameters_dont_match" to "Function for operator with id '{0}' must have {1} parameters",
        "runtime.function.operator.no_return_value" to "Operator function must have return value",

        "runtime.constructor.doesnt_exist" to "Requested constructor for class with id '{0}' doesn't exist",
        "runtime.constructor.already_exists" to "Constructor with these parameters already exists",
        "runtime.constructor.cant_access" to "Can't access requested constructor for class with id '{0}'",

        "runtime.variable.doesnt_exist" to "Variable with id '{0}' doesn't exist",
        "runtime.variable.cant_find" to "Can't find variable with id '{0}'",
        "runtime.variable.already_exists" to "Variable with id '{0}' already exists",
        "runtime.variable.cant_access" to "Can't access variable with id '{0}'",
        "runtime.variable.not_initialized" to "Constant variable with id '{0}' hasn't been initialized",
        "runtime.variable.cant_reassign" to "Can't reassign value of constant variable with id '{0}'",
        "runtime.variable.invalid_data_type" to "Variable with id '{0}' requires data type '{1}'",

        "runtime.member.null" to "Can't get member of 'null'",
        "runtime.member.not_class" to "Can't get member of '{0}' because it's not a class",

        "runtime.this.not_inside_class" to "Can't use 'this' keyword not inside a class",
        "runtime.this.shared" to "Can't use 'this' keyword inside a shared environment",

        "runtime.return_value.must_return" to "Function with id '{0}' must return value but doesn't",
        "runtime.return_value.must_not_return" to "Function with id '{0}' must not return value but does",
        "runtime.return_value.different_data_type" to "Function with id '{0}' returned value of different data type (expected {1})"
    )

    /**
     * @param key Translation key
     * @return Translation that corresponds to given key
     */
    operator fun get(key: String) = translations[key]
}