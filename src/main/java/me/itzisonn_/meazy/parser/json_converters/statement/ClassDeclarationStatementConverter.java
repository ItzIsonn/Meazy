package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ClassDeclarationStatementConverter extends Converter<ClassDeclarationStatement> {
    public ClassDeclarationStatementConverter() {
        super(RegistryIdentifier.ofDefault("class_declaration_statement"));
    }

    @Override
    public ClassDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String id = getElement(object, "id").getAsString();

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ClassDeclarationStatement(id, body);
    }

    @Override
    public JsonElement serialize(ClassDeclarationStatement classDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", classDeclarationStatement.getId());

        JsonArray body = new JsonArray();
        for (Statement statement : classDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}