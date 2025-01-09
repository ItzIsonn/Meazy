package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ProgramConverter extends Converter<Program> {
    @Override
    public Program deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("version") == null) throw new InvalidCompiledFileException(getIdentifier(), "version");
        String version = object.get("version").getAsString();

        if (object.get("body") == null) throw new InvalidCompiledFileException(getIdentifier(), "body");
        List<Statement> body = object.get("body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new Program(version, body);
    }

    @Override
    public JsonElement serialize(Program program, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("version", program.getVersion());

        JsonArray body = new JsonArray();
        for (Statement statement : program.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("program");
    }
}