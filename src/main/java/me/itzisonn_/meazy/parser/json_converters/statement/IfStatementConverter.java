package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.IfStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class IfStatementConverter extends Converter<IfStatement> {
    @Override
    public IfStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("condition") == null) throw new InvalidCompiledFileException(getIdentifier(), "condition");
        Expression condition = jsonDeserializationContext.deserialize(object.get("condition"), Expression.class);

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        IfStatement elseStatement = null;
        if (object.get("else_statement") != null) {
            elseStatement = jsonDeserializationContext.deserialize(object.get("else_statement"), IfStatement.class);
        }

        return new IfStatement(condition, body, elseStatement);
    }

    @Override
    public JsonElement serialize(IfStatement ifStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

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
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("if_statement");
    }
}