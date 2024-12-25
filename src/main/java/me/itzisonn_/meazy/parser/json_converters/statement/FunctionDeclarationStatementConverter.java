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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FunctionDeclarationStatementConverter implements Converter<FunctionDeclarationStatement> {
    @Override
    public FunctionDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("function_declaration_statement")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("FunctionDeclarationStatement doesn't have field id");
            String id = object.get("id").getAsString();

            if (object.get("args") == null) throw new InvalidCompiledFileException("FunctionDeclarationStatement doesn't have field args");
            ArrayList<CallArgExpression> args = new ArrayList<>();
            for (JsonElement arg : object.get("args").getAsJsonArray()) {
                args.add(jsonDeserializationContext.deserialize(arg, CallArgExpression.class));
            }

            if (object.get("body") == null) throw new InvalidCompiledFileException("FunctionDeclarationStatement doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            DataType dataType = null;
            if (object.get("return_data_type") != null) {
                dataType = DataTypes.parse(object.get("return_data_type").getAsString());
            }

            Expression arraySize = null;
            if (object.get("array_size") != null) {
                arraySize = jsonDeserializationContext.deserialize(object.get("array_size"), Expression.class);
            }

            if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException("FunctionDeclarationStatement doesn't have field access_modifiers");
            Set<AccessModifier> accessModifiers = new HashSet<>();
            for (JsonElement accessModifier : object.get("access_modifiers").getAsJsonArray()) {
                accessModifiers.add(AccessModifiers.parse(accessModifier.getAsString()));
            }

            return new FunctionDeclarationStatement(id, args, body, dataType, arraySize, accessModifiers);
        }

        throw new InvalidCompiledFileException("Can't deserialize FunctionDeclarationStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(FunctionDeclarationStatement functionDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "function_declaration_statement");

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
    public String getId() {
        return "function_declaration_statement";
    }
}