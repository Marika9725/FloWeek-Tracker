package com.floweektracker.repository;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Adapter for serializing and deserializing {@link LocalTime} objects into and from JSON file.
 * <br><br>
 * FIELDS: {@link #FORMATTER}
 * <br><br>
 * METHODS: {@link #serialize(LocalTime, Type, JsonSerializationContext)},
 * {@link #deserialize(JsonElement, Type, JsonDeserializationContext)}
 */
public class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
    /**
     * Formatter with specified date time pattern, used to serialize and deserialize {@link LocalTime} objects.
     *
     * @see #serialize(LocalTime, Type, JsonSerializationContext)
     * @see #deserialize(JsonElement, Type, JsonDeserializationContext)
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Serializes a {@link LocalTime} object to JSON using the {@link #FORMATTER}.
     *
     * @param time the {@link LocalTime} object to be serialized
     * @param typeOfSrc the actual type (fully genericized version) of the source object.
     * @param context the context of serialization process, used for delegating nested serialization
     * @return a {@link JsonElement} representing the serialized {@link LocalTime} object.
     */
    @Override
    public JsonElement serialize(LocalTime time, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(time.format(FORMATTER));
    }

    /**
     * Deserializes a {@link LocalTime} object from JSON data using the {@link #FORMATTER}.
     *
     * @param json the Json data being deserialized
     * @param typeOfT the type of the object to deserialize to
     * @param context the context of the deserialization process, used to deserialize nested objects (not used here)
     * @return a {@link LocalTime} object representing the deserialized JSON data
     */
    @Override
    public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalTime.parse(json.getAsString(), FORMATTER);
    }
}