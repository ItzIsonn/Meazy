package me.itzisonn_.meazy.parser.json_converter.basic;

import com.google.gson.*;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;
import me.itzisonn_.meazy.version.Version;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProgramConverter extends Converter<Program> {
    public ProgramConverter() {
        super(MeazyMain.getDefaultIdentifier("program"));
    }

    @Override
    public Program deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        String version = getElement(object, "version").getAsString();

        Map<String, Version> requiredAddons = new HashMap<>();
        if (object.get("required_addons") != null) {
            Map<String, JsonElement> map = getElement(object, "required_addons").getAsJsonObject().asMap();

            for (String addonId : map.keySet()) {
                if (!(map.get(addonId) instanceof JsonPrimitive jsonPrimitive)) throw new InvalidCompiledFileException("Addon version must be String");

                Version addonVersion;
                if (jsonPrimitive.isBoolean()) addonVersion = null;
                else addonVersion = Version.of(jsonPrimitive.getAsString());

                requiredAddons.put(addonId, addonVersion);
            }
        }

        List<Statement> body = getElement(object, "body").getAsJsonArray().asList().stream().map(statement ->
                (Statement) jsonDeserializationContext.deserialize(statement, Statement.class)).collect(Collectors.toList());

        return new Program(Version.of(version), requiredAddons, body);
    }

    @Override
    public JsonElement serialize(Program program, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("version", program.getVersion().toString());

        JsonObject requiredAddons = new JsonObject();
        for (String addonId : program.getRequiredAddons().keySet()) {
            Version version = program.getRequiredAddons().get(addonId);
            if (version != null) requiredAddons.addProperty(addonId, version.toString());
            else requiredAddons.addProperty(addonId, true);
        }
        result.add("required_addons", requiredAddons);

        JsonArray body = new JsonArray();
        for (Statement statement : program.getBody()) {
            body.add(jsonSerializationContext.serialize(statement));
        }
        result.add("body", body);

        return result;
    }
}