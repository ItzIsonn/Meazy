package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class CallArgExpressionConverter implements Converter<CallArgExpression> {
    @Override
    public CallArgExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("call_arg_expression")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("CallArgExpression doesn't have field id");
            String id = object.get("id").getAsString();

            if (object.get("data_type") == null) throw new InvalidCompiledFileException("CallArgExpression doesn't have field data_type");
            DataType dataType = DataTypes.parse(object.get("data_type").getAsString());

            if (object.get("is_constant") == null) throw new InvalidCompiledFileException("CallArgExpression doesn't have field is_constant");
            boolean isConstant = object.get("is_constant").getAsBoolean();

            return new CallArgExpression(id, dataType, isConstant);
        }

        throw new InvalidCompiledFileException("Can't deserialize CallArgExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(CallArgExpression callArgExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "call_arg_expression");

        result.addProperty("id", callArgExpression.getId());
        result.addProperty("data_type", callArgExpression.getDataType().getName());
        result.addProperty("is_constant", callArgExpression.isConstant());

        return result;
    }
}