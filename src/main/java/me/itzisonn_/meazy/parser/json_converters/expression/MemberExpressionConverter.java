package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class MemberExpressionConverter extends Converter<MemberExpression> {
    public MemberExpressionConverter() {
        super(RegistryIdentifier.ofDefault("member_expression"));
    }

    @Override
    public MemberExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("object") == null) throw new InvalidCompiledFileException(getIdentifier(), "object");
        Expression objectExpression = jsonDeserializationContext.deserialize(object.get("object"), Expression.class);

        if (object.get("member") == null) throw new InvalidCompiledFileException(getIdentifier(), "member");
        Expression member = jsonDeserializationContext.deserialize(object.get("member"), Expression.class);

        return new MemberExpression(objectExpression, member);
    }

    @Override
    public JsonElement serialize(MemberExpression memberExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("object", jsonSerializationContext.serialize(memberExpression.getObject()));
        result.add("member", jsonSerializationContext.serialize(memberExpression.getMember()));

        return result;
    }
}