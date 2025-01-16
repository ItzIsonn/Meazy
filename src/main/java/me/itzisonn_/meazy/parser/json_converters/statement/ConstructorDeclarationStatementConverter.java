package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
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

        if (object.get("args") == null) throw new InvalidCompiledFileException(getIdentifier(), "args");
        List<CallArgExpression> args = object.get("args").getAsJsonArray().asList().stream().map(arg ->
                (CallArgExpression) jsonDeserializationContext.deserialize(arg, CallArgExpression.class)).collect(Collectors.toList());

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException(getIdentifier(), "access_modifiers");
        Set<AccessModifier> accessModifiers = object.get("access_modifiers").getAsJsonArray().asList().stream().map(accessModifier ->
                AccessModifiers.parse(accessModifier.getAsString())).collect(Collectors.toSet());

        return new ConstructorDeclarationStatement(args, body, accessModifiers);
    }

    @Override
    public JsonElement serialize(ConstructorDeclarationStatement constructorDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

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

        JsonArray accessModifiers = new JsonArray();
        for (AccessModifier accessModifier : constructorDeclarationStatement.getAccessModifiers()) {
            accessModifiers.add(accessModifier.getId());
        }
        result.add("access_modifiers", accessModifiers);

        return result;
    }
}