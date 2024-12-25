package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.ForStatement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ForStatementConverter implements Converter<ForStatement> {
    @Override
    public ForStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("for_statement")) {
            VariableDeclarationStatement variableDeclarationStatement = null;
            if (object.get("variable_declaration") != null) {
                variableDeclarationStatement = jsonDeserializationContext.deserialize(object.get("variable_declaration"), VariableDeclarationStatement.class);
            }

            Expression condition = null;
            if (object.get("condition") != null) {
                condition = jsonDeserializationContext.deserialize(object.get("condition"), Expression.class);
            }

            AssignmentExpression assignmentExpression = null;
            if (object.get("assignment_expression") != null) {
                assignmentExpression = jsonDeserializationContext.deserialize(object.get("assignment_expression"), AssignmentExpression.class);
            }

            if (object.get("body") == null) throw new InvalidCompiledFileException("ForStatement doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
        }

        throw new InvalidCompiledFileException("Can't deserialize ForStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ForStatement forStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "for_statement");

        if (forStatement.getVariableDeclarationStatement() != null)
            result.add("variable_declaration", jsonSerializationContext.serialize(forStatement.getVariableDeclarationStatement()));
        if (forStatement.getCondition() != null)
            result.add("condition", jsonSerializationContext.serialize(forStatement.getCondition()));
        if (forStatement.getAssignmentExpression() != null)
            result.add("assignment_expression", jsonSerializationContext.serialize(forStatement.getAssignmentExpression()));

        JsonArray body = new JsonArray();
        for (Statement statement : forStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement, Statement.class));
        }
        result.add("body", body);

        return result;
    }

    @Override
    public String getId() {
        return "ForStatement";
    }
}