package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ForeachStatementConverter extends Converter<ForeachStatement> {
    @Override
    public ForeachStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("variable_declaration") == null) throw new InvalidCompiledFileException(getIdentifier(), "variable_declaration");
        VariableDeclarationStatement variableDeclarationStatement = jsonDeserializationContext.deserialize(object.get("variable_declaration"), VariableDeclarationStatement.class);

        if (object.get("collection") == null) throw new InvalidCompiledFileException(getIdentifier(), "collection");
        Expression collection = jsonDeserializationContext.deserialize(object.get("collection"), Expression.class);

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ForeachStatement(variableDeclarationStatement, collection, body);
    }

    @Override
    public JsonElement serialize(ForeachStatement foreachStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("variable_declaration", jsonSerializationContext.serialize(foreachStatement.getVariableDeclarationStatement()));
        result.add("collection", jsonSerializationContext.serialize(foreachStatement.getCollection()));

        JsonArray body = new JsonArray();
        for (Statement statement : foreachStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement, Statement.class));
        }
        result.add("body", body);

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("foreach_statement");
    }
}