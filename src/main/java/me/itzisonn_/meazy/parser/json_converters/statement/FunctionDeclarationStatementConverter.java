package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FunctionDeclarationStatementConverter extends Converter<FunctionDeclarationStatement> {
    @Override
    public FunctionDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("id") == null) throw new InvalidCompiledFileException(getIdentifier(), "id");
        String id = object.get("id").getAsString();

        if (object.get("args") == null) throw new InvalidCompiledFileException(getIdentifier(), "args");
        List<CallArgExpression> args = object.get("args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        DataType dataType = null;
        if (object.get("return_data_type") != null) {
            dataType = DataTypes.parse(object.get("return_data_type").getAsString());
        }

        Expression arraySize = null;
        if (object.get("array_size") != null) {
            arraySize = jsonDeserializationContext.deserialize(object.get("array_size"), Expression.class);
        }

        if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException(getIdentifier(), "access_modifiers");
        Set<AccessModifier> accessModifiers = object.get("access_modifiers").getAsJsonArray().asList().stream().map(accessModifier ->
                AccessModifiers.parse(accessModifier.getAsString())).collect(Collectors.toSet());

        return new FunctionDeclarationStatement(id, args, body, dataType, arraySize, accessModifiers);
    }

    @Override
    public JsonElement serialize(FunctionDeclarationStatement functionDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

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
            result.addProperty("return_data_type", functionDeclarationStatement.getReturnDataType().getName());
        }

        if (functionDeclarationStatement.getArraySize() != null) result.add("array_size", jsonSerializationContext.serialize(functionDeclarationStatement.getArraySize()));

        JsonArray accessModifiers = new JsonArray();
        for (AccessModifier accessModifier : functionDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier.getId());
        }
        result.add("access_modifiers", accessModifiers);

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("function_declaration_statement");
    }
}