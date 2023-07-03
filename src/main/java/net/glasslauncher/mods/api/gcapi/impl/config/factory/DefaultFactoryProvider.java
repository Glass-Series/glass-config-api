package net.glasslauncher.mods.api.gcapi.impl.config.factory;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.BooleanConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.FloatConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.FloatListConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.IntegerConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.IntegerListConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.StringConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.StringListConfigEntry;
import uk.co.benjiweber.expressions.function.OctFunction;

import java.lang.reflect.*;
import java.util.function.*;

public class DefaultFactoryProvider implements ConfigFactoryProvider {

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, OctFunction<String, String, String, Field, Object, Boolean, Object, MaxLength, ConfigEntry<?>>> immutableBuilder) {
        immutableBuilder.put(String.class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new StringConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, value.toString(), maxLength)));
        immutableBuilder.put(Integer.class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new IntegerConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, Integer.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Float.class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new FloatConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, Float.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Boolean.class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new BooleanConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, (boolean) value)));
        immutableBuilder.put(String[].class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new StringListConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, (String[]) value, maxLength))); // the new ArrayList is required or it returns java.util.Arrays.ArrayList, which is fucking dumb.
        immutableBuilder.put(Integer[].class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new IntegerListConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, (Integer[]) value, maxLength)));
        immutableBuilder.put(Float[].class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength) -> new FloatListConfigEntry(id, name, description, parentField, parentObject, isMultiplayerSynced, (Float[]) value, maxLength)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, JsonElement>> immutableBuilder) {
        immutableBuilder.put(String.class, JsonPrimitive::new);
        immutableBuilder.put(Integer.class, JsonPrimitive::new);
        immutableBuilder.put(Float.class, JsonPrimitive::new);
        immutableBuilder.put(Boolean.class, JsonPrimitive::new);
        immutableBuilder.put(String[].class, this::serializeArray);
        immutableBuilder.put(Integer[].class, this::serializeArray);
        immutableBuilder.put(Float[].class, this::serializeArray);
    }

    // CURSED AS ALWAYS
    private <T>JsonElement serializeArray(Object object) {
        JsonArray array = new JsonArray();
        //noinspection unchecked
        for (T value : (T[]) object) {
            array.add(new JsonPrimitive(value));
        }
        return array;
    }
}
