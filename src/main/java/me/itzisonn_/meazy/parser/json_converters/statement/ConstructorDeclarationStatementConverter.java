package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConstructorDeclarationStatementConverter extends Converter<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementConverter() {
        super(RegistryIdentifier.ofDefault("constructor_declaration_statement"));
    }

    @Override
    public ConstructorDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Set<String> accessModifiers = getElement(object, "access_modifiers").getAsJsonArray().asList().stream().map(accessModifier -> {
            if (Registries.ACCESS_MODIFIERS.hasEntry(accessModifier.getAsString())) {
                throw new InvalidCompiledFileException("Unknown access modifier with id " + accessModifier.getAsString());
            }
            return accessModifier.getAsString();
        }).collect(Collectors.toSet());

        List<CallArgExpression> args = getElement(object, "args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new ConstructorDeclarationStatement(accessModifiers, args, body);
    }

    @Override
    public JsonElement serialize(ConstructorDeclarationStatement constructorDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray accessModifiers = new JsonArray();
        for (String accessModifier : constructorDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier);
        }
        result.add("access_modifiers", accessModifiers);

        JsonArray args = new JsonArray();
        for (CallArgExpression arg : constructorDeclarationStatement.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        JsonArray body = new JsonArray();
        for (Statement statement : constructorDeclarationStatement.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}