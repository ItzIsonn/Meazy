package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class VariableDeclarationConverter implements Converter<VariableDeclarationStatement> {
    @Override
    public VariableDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("variable_declaration_statement")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("VariableDeclarationStatement doesn't have field id");
            String id = object.get("id").getAsString();

            Expression arraySize = null;
            if (object.get("array_size") != null) {
                arraySize = jsonDeserializationContext.deserialize(object.get("array_size"), Expression.class);
            }

            if (object.get("data_type") == null) throw new InvalidCompiledFileException("VariableDeclarationStatement doesn't have field data_type");
            DataType dataType = DataTypes.parse(object.get("data_type").getAsString());

            Expression value = null;
            if (object.get("value") != null) {
                value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);
            }

            if (object.get("is_constant") == null) throw new InvalidCompiledFileException("VariableDeclarationStatement doesn't have field is_constant");
            boolean isConstant = object.get("is_constant").getAsBoolean();

            if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException("VariableDeclarationStatement doesn't have field access_modifiers");
            Set<AccessModifier> accessModifiers = new HashSet<>();
            for (JsonElement accessModifier : object.get("access_modifiers").getAsJsonArray()) {
                accessModifiers.add(AccessModifiers.parse(accessModifier.getAsString()));
            }

            return new VariableDeclarationStatement(id, arraySize, dataType, value, isConstant, accessModifiers);
        }

        throw new InvalidCompiledFileException("Can't deserialize VariableDeclarationStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(VariableDeclarationStatement variableDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "variable_declaration_statement");

        result.addProperty("id", variableDeclarationStatement.getId());
        if (variableDeclarationStatement.getArraySize() != null) result.add("array_size", jsonSerializationContext.serialize(variableDeclarationStatement.getArraySize()));
        result.addProperty("data_type", variableDeclarationStatement.getDataType().getName());
        if (variableDeclarationStatement.getValue() != null) result.add("value", jsonSerializationContext.serialize(variableDeclarationStatement.getValue()));
        result.addProperty("is_constant", variableDeclarationStatement.isConstant());

        JsonArray accessModifiers = new JsonArray();
        for (AccessModifier accessModifier : variableDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier.getId());
        }
        result.add("access_modifiers", accessModifiers);

        return result;
    }

    @Override
    public String getId() {
        return "variable_declaration_statement";
    }
}