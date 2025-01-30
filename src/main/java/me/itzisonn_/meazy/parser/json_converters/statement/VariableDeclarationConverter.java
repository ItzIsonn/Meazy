package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VariableDeclarationConverter extends Converter<VariableDeclarationStatement> {
    public VariableDeclarationConverter() {
        super(RegistryIdentifier.ofDefault("variable_declaration_statement"));
    }

    @Override
    public VariableDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException(getIdentifier(), "access_modifiers");
        Set<String> accessModifiers = object.get("access_modifiers").getAsJsonArray().asList().stream().map(accessModifier -> {
            if (Registries.ACCESS_MODIFIERS.hasEntry(accessModifier.getAsString())) {
                throw new InvalidCompiledFileException("Unknown access modifier with id " + accessModifier.getAsString());
            }
            return accessModifier.getAsString();
        }).collect(Collectors.toSet());

        if (object.get("is_constant") == null) throw new InvalidCompiledFileException(getIdentifier(), "is_constant");
        boolean isConstant = object.get("is_constant").getAsBoolean();

        if (object.get("declaration_infos") == null) throw new InvalidCompiledFileException(getIdentifier(), "declaration_infos");
        List<VariableDeclarationStatement.VariableDeclarationInfo> declarationInfos = object.get("declaration_infos").getAsJsonArray().asList().stream().map(element -> {
            JsonObject declarationObject = element.getAsJsonObject();

            if (declarationObject.get("id") == null) throw new InvalidCompiledFileException(getIdentifier(), "id");
            String id = declarationObject.get("id").getAsString();

            if (declarationObject.get("data_type") == null) throw new InvalidCompiledFileException(getIdentifier(), "data_type");
            String dataType = declarationObject.get("data_type").getAsString();

            Expression value = null;
            if (declarationObject.get("value") != null) {
                value = jsonDeserializationContext.deserialize(declarationObject.get("value"), Expression.class);
            }

            return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, value);
        }).toList();

        return new VariableDeclarationStatement(accessModifiers, isConstant, declarationInfos);
    }

    @Override
    public JsonElement serialize(VariableDeclarationStatement variableDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray accessModifiers = new JsonArray();
        for (String accessModifier : variableDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier);
        }
        result.add("access_modifiers", accessModifiers);

        result.addProperty("is_constant", variableDeclarationStatement.isConstant());

        JsonArray declarationInfos = new JsonArray();
        for (VariableDeclarationStatement.VariableDeclarationInfo declarationInfo : variableDeclarationStatement.getDeclarationInfos()) {
            JsonObject declarationObject = new JsonObject();
            declarationObject.addProperty("id", declarationInfo.getId());
            declarationObject.addProperty("data_type", declarationInfo.getDataType());
            if (declarationInfo.getValue() != null) declarationObject.add("value", jsonSerializationContext.serialize(declarationInfo.getValue()));
            declarationInfos.add(declarationObject);
        }
        result.add("declaration_infos", declarationInfos);


        return result;
    }
}