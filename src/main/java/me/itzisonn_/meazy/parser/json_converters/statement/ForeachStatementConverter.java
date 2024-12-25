package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ForeachStatementConverter implements Converter<ForeachStatement> {
    @Override
    public ForeachStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("foreach_statement")) {
            if (object.get("variable_declaration") == null) throw new InvalidCompiledFileException("ForeachStatement doesn't have field variable_declaration");
            VariableDeclarationStatement variableDeclarationStatement = jsonDeserializationContext.deserialize(object.get("variable_declaration"), VariableDeclarationStatement.class);

            if (object.get("collection") == null) throw new InvalidCompiledFileException("ForeachStatement doesn't have field collection");
            Expression collection = jsonDeserializationContext.deserialize(object.get("collection"), Expression.class);

            if (object.get("body") == null) throw new InvalidCompiledFileException("ForeachStatement doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            return new ForeachStatement(variableDeclarationStatement, collection, body);
        }

        throw new InvalidCompiledFileException("Can't deserialize ForeachStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ForeachStatement foreachStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "foreach_statement");

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
    public String getId() {
        return "foreach_statement";
    }
}