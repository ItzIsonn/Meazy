package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ProgramConverter implements Converter<Program> {
    @Override
    public Program deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("program")) {
            if (object.get("version") == null) throw new InvalidCompiledFileException("Program doesn't have field version");
            String version = object.get("version").getAsString();

            if (object.get("body") == null) throw new InvalidCompiledFileException("Program doesn't have field body");
            ArrayList<Statement> body = new ArrayList<>();
            for (JsonElement statement : object.get("body").getAsJsonArray()) {
                body.add(jsonDeserializationContext.deserialize(statement, Statement.class));
            }

            return new Program(version, body);
        }

        throw new InvalidCompiledFileException("Can't deserialize Program because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(Program program, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "program");

        result.addProperty("version", program.getVersion());

        JsonArray body = new JsonArray();
        for (Statement statement : program.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }

    @Override
    public String getId() {
        return "program";
    }
}