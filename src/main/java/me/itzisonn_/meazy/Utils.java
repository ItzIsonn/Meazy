package me.itzisonn_.meazy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.json_converters.expression.*;
import me.itzisonn_.meazy.parser.json_converters.expression.call_expression.ClassCallExpressionConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.call_expression.FunctionCallExpressionConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.ClassIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.FunctionIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.VariableIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.BooleanLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.NullLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.NumberLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.StringLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.statement.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Utility class
 */
public class Utils {
    private Utils() {}

    /**
     * Regex used by all identifiers
     */
    public static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**
     * System.in Scanner
     */
    public static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Random
     */
    public static final Random RANDOM = new Random();

    /**
     * Gson with all json converters
     * @see me.itzisonn_.meazy.parser.json_converters
     */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(BreakStatement .class, new BreakStatementConverter())
            .registerTypeAdapter(ClassDeclarationStatement .class, new ClassDeclarationStatementConverter())
            .registerTypeAdapter(ConstructorDeclarationStatement .class, new ConstructorDeclarationStatementConverter())
            .registerTypeAdapter(ContinueStatement .class, new ContinueStatementConverter())
            .registerTypeAdapter(ForStatement .class, new ForStatementConverter())
            .registerTypeAdapter(FunctionDeclarationStatement.class, new FunctionDeclarationStatementConverter())
            .registerTypeAdapter(IfStatement.class, new IfStatementConverter())
            .registerTypeAdapter(Program.class, new ProgramConverter())
            .registerTypeAdapter(ReturnStatement.class, new ReturnStatementConverter())
            .registerTypeAdapter(VariableDeclarationStatement.class, new VariableDeclarationConverter())
            .registerTypeAdapter(WhileStatement.class, new WhileStatementConverter())

            .registerTypeAdapter(BooleanLiteral .class, new BooleanLiteralConverter())
            .registerTypeAdapter(NullLiteral .class, new NullLiteralConverter())
            .registerTypeAdapter(NumberLiteral .class, new NumberLiteralConverter())
            .registerTypeAdapter(StringLiteral .class, new StringLiteralConverter())

            .registerTypeAdapter(AssignmentExpression .class, new AssignmentExpressionConverter())
            .registerTypeAdapter(BinaryExpression .class, new BinaryExpressionConverter())
            .registerTypeAdapter(CallArgExpression .class, new CallArgExpressionConverter())
            .registerTypeAdapter(ComparisonExpression .class, new ComparisonExpressionConverter())
            .registerTypeAdapter(LogicalExpression .class, new LogicalExpressionConverter())
            .registerTypeAdapter(MemberExpression.class, new MemberExpressionConverter())
            .registerTypeAdapter(ClassCallExpression .class, new ClassCallExpressionConverter())
            .registerTypeAdapter(FunctionCallExpression .class, new FunctionCallExpressionConverter())
            .registerTypeAdapter(ClassIdentifier .class, new ClassIdentifierConverter())
            .registerTypeAdapter(FunctionIdentifier .class, new FunctionIdentifierConverter())
            .registerTypeAdapter(VariableIdentifier .class, new VariableIdentifierConverter())

            .registerTypeAdapter(Statement.class, new StatementConverter())
            .registerTypeAdapter(Expression.class, new ExpressionConverter())

            .create();

    /**
     * Counts number of target's matches in given string
     *
     * @param string The string to count in
     * @param target The target to match
     * @return Number of target's matches in given string
     */
    public static int countMatches(String string, String target) {
        if (string == null || target == null) throw new NullPointerException("Neither of strings can't be null!");
        return (string.length() - string.replace(target, "").length()) / target.length();
    }

    /**
     * Returns extension of given file
     *
     * @param file Target file
     * @return Extension of file
     * @throws NullPointerException When given file is null
     */
    public static String getExtension(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null!");
        String name = file.getName();

        int i = name.lastIndexOf('.');
        if (i > 0) {
            return name.substring(i + 1);
        }
        return "";
    }

    /**
     * Returns lines of given file
     *
     * @param file Target file
     * @return Lines of file
     * @throws NullPointerException When given file is null
     */
    public static String getLines(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null!");

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();

            while (line != null) {
                lines.add(line.trim());
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        StringBuilder string = new StringBuilder();
        for (String line : lines) {
            string.append(line).append("\n");
        }
        return string.toString();
    }

    /**
     * Compares to versions in format 'a.b.c...'
     *
     * @param version1 Version to compare
     * @param version2 Version to compare
     * @return True if version2 was released after version1, otherwise false
     * @throws NullPointerException When either of versions is null
     * @throws NumberFormatException When either of version have non-integer part
     */
    public static boolean isVersionAfter(String version1, String version2) throws NullPointerException, NumberFormatException {
        if (version1 == null || version2 == null) throw new NullPointerException("Neither of versions can't be null");

        String[] split1 = version1.split("\\.");
        String[] split2 = version2.split("\\.");
        for (int i = 0; i < Math.max(split1.length, split2.length); i++) {
            int part1, part2;
            try {
                part1 = i < split1.length ? Integer.parseInt(split1[i]) : 0;
                part2 = i < split2.length ? Integer.parseInt(split2[i]) : 0;
            }
            catch (NumberFormatException ignore) {
                throw new IllegalArgumentException("Version parts must be integers");
            }
            if (part1 < part2) return true;
            if (part1 > part2) return false;
        }
        return false;
    }

    /**
     * Returns offset represented by a string
     *
     * @param offset Number of offsets
     * @return String offset
     * @throws IllegalArgumentException When given offset is negative
     */
    public static String getOffset(int offset) throws IllegalArgumentException {
        if (offset < 0) throw new IllegalArgumentException("Offset can't be negative");

        return "\t".repeat(offset);
    }
}