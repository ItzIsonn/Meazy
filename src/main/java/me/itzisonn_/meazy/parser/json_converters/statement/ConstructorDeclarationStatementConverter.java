package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConstructorDeclarationStatementConverter implements Converter<ConstructorDeclarationStatement> {
    @Override
    public ConstructorDeclarationStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("constructor_declaration_statement")) {
            if (object.get("args") == null) throw new InvalidCompiledFileException("ConstructorDeclarationStatement doesn't have field args");
            ArrayList<CallArgExpression> args = new ArrayList<>();
            for (JsonElement arg : object.get("args").getAsJsonArray()) {
                args.add(jsonDeserializationContext.deserialize(arg, CallArgExpression.class));
            }

            if (object.get("body") == null) throw new InvalidCompiledFileException("ConstructorDeclarationStatement doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            if (object.get("access_modifiers") == null) throw new InvalidCompiledFileException("ConstructorDeclarationStatement doesn't have field access_modifiers");
            Set<AccessModifier> accessModifiers = new HashSet<>();
            for (JsonElement accessModifier : object.get("access_modifiers").getAsJsonArray()) {
                accessModifiers.add(AccessModifiers.parse(accessModifier.getAsString()));
            }

            return new ConstructorDeclarationStatement(args, body, accessModifiers);
        }

        throw new InvalidCompiledFileException("Can't deserialize ConstructorDeclarationStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ConstructorDeclarationStatement constructorDeclarationStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "constructor_declaration_statement");

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