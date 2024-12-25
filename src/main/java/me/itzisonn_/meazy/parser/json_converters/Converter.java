package me.itzisonn_.meazy.parser.json_converters;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface Converter<T> extends JsonDeserializer<T>, JsonSerializer<T> {
    String getId();
}