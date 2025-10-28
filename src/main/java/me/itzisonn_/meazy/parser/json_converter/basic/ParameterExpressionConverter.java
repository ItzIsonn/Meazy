package me.itzisonn_.meazy.parser.json_converter.basic;

import com.google.gson.*;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.json_converter.Converter;

import java.lang.reflect.Type;

public class ParameterExpressionConverter extends Converter<ParameterExpression> {
    public ParameterExpressionConverter() {
        super(MeazyMain.getDefaultIdentifier("parameter_expression"));
    }

    @Override
    public ParameterExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String id = getElement(object, "id").getAsString();

        JsonObject dataTypeObject = getElement(object, "data_type").getAsJsonObject();
        String dataTypeId = getElement(dataTypeObject, "id", "data_type.id").getAsString();
        boolean dataTypeIsNullable = getElement(dataTypeObject, "is_nullable", "data_type.is_nullable").getAsBoolean();
        DataType dataType = Registries.DATA_TYPE_FACTORY.getEntry().getValue().create(dataTypeId, dataTypeIsNullable);

        boolean isConstant = getElement(object, "is_constant").getAsBoolean();

        return new ParameterExpression(id, dataType, isConstant);
    }

    @Override
    public JsonElement serialize(ParameterExpression parameterExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", parameterExpression.getId());

        JsonObject dataTypeObject = new JsonObject();
        dataTypeObject.addProperty("id", parameterExpression.getDataType().getId());
        dataTypeObject.addProperty("is_nullable", parameterExpression.getDataType().isNullable());
        result.add("data_type", dataTypeObject);

        result.addProperty("is_constant", parameterExpression.isConstant());

        return result;
    }
}