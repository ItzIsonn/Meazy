package me.itzisonn_.meazy.parser.json_converters.expression.call_expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ClassCallExpressionConverter extends Converter<ClassCallExpression> {
    public ClassCallExpressionConverter() {
        super(RegistryIdentifier.ofDefault("class_call_expression"));
    }

    @Override
    public ClassCallExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("caller") == null) throw new InvalidCompiledFileException(getIdentifier(), "caller");
        Expression caller = jsonDeserializationContext.deserialize(object.get("caller"), Expression.class);

        if (object.get("args") == null) throw new InvalidCompiledFileException(getIdentifier(), "args");
        List<Expression> args = object.get("args").getAsJsonArray().asList().stream().map(arg ->
                (Expression) jsonDeserializationContext.deserialize(arg, Expression.class)).collect(Collectors.toList());

        return new ClassCallExpression(caller, args);
    }

    @Override
    public JsonElement serialize(ClassCallExpression classCallExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("caller", jsonSerializationContext.serialize(classCallExpression.getCaller()));

        JsonArray args = new JsonArray();
        for (Expression arg : classCallExpression.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }
}