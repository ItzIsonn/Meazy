package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionDeclarationStatementConverter extends Converter<FunctionDeclarationStatement> {
    public FunctionDeclarationStatementConverter() {
        super(RegistryIdentifier.ofDefault("function_declaration_statement"));
    }

    @Override
    public FunctionDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException(getIdentifier(), "access_modifiers");
        Set<String> accessModifiers = object.get("access_modifiers").getAsJsonArray().asList().stream().map(accessModifier -> {
            if (Registries.ACCESS_MODIFIERS.hasEntry(accessModifier.getAsString())) {
                throw new InvalidCompiledFileException("Unknown access modifier with id " + accessModifier.getAsString());
            }
            return accessModifier.getAsString();
        }).collect(Collectors.toSet());

        if (object.get("id") == null) throw new InvalidCompiledFileException(getIdentifier(), "id");
        String id = object.get("id").getAsString();

        if (object.get("args") == null) throw new InvalidCompiledFileException(getIdentifier(), "args");
        List<CallArgExpression> args = object.get("args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        String dataType = null;
        if (object.get("return_data_type") != null) {
            dataType = object.get("return_data_type").getAsString();
        }

        return new FunctionDeclarationStatement(accessModifiers, id, args, body, dataType);
    }

    @Override
    public JsonElement serialize(FunctionDeclarationStatement functionDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray accessModifiers = new JsonArray();
        for (String accessModifier : functionDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier);
        }
        result.add("access_modifiers", accessModifiers);

        result.addProperty("id", functionDeclarationStatement.getId());

        JsonArray args = new JsonArray();
        for (CallArgExpression arg : functionDeclarationStatement.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        JsonArray body = new JsonArray();
        for (Statement statement : functionDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        if (functionDeclarationStatement.getReturnDataType() != null) {
            result.addProperty("return_data_type", functionDeclarationStatement.getReturnDataType());
        }

        return result;
    }
}