package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.IfStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class IfStatementConverter implements Converter<IfStatement> {
    @Override
    public IfStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("if_statement")) {
            if (object.get("condition") == null) throw new InvalidCompiledFileException("IfStatement doesn't have field condition");
            Expression condition = jsonDeserializationContext.deserialize(object.get("condition"), Expression.class);

            if (object.get("body") == null) throw new InvalidCompiledFileException("IfStatement doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            IfStatement elseStatement = null;
            if (object.get("else_statement") != null) {
                elseStatement = jsonDeserializationContext.deserialize(object.get("else_statement"), IfStatement.class);
            }

            return new IfStatement(condition, body, elseStatement);
        }

        throw new InvalidCompiledFileException("Can't deserialize IfStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(IfStatement ifStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "if_statement");

        result.add("condition", jsonSerializationContext.serialize(ifStatement.getCondition()));

        JsonArray body = new JsonArray();
        for (Statement statement : ifStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        if (ifStatement.getElseStatement() != null) {
            result.add("else_statement", jsonSerializationContext.serialize(ifStatement.getElseStatement()));
        }

        return result;
    }

    @Override
    public String getId() {
        return "if_statement";
    }
}