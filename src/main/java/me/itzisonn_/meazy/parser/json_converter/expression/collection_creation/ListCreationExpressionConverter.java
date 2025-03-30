package me.itzisonn_.meazy.parser.json_converter.expression.collection_creation;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ListCreationExpressionConverter extends Converter<ListCreationExpression> {
    public ListCreationExpressionConverter() {
        super(RegistryIdentifier.ofDefault("list_creation_expression"));
    }

    @Override
    public ListCreationExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        List<Expression> list = getElement(object, "list").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new ListCreationExpression(list);
    }

    @Override
    public JsonElement serialize(ListCreationExpression listCreationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray list = new JsonArray();
        for (Expression element : listCreationExpression.getList()) {
            list.add(jsonSerializationContext.serialize(element));
        }
        result.add("list", list);

        return result;
    }
}