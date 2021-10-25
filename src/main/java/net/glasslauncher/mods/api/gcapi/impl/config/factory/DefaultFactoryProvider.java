package net.glasslauncher.mods.api.gcapi.impl.config.factory;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.impl.ConfigFactories;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.BooleanConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.FloatConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.IntegerConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ListStringEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.StringConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class DefaultFactoryProvider implements ConfigFactoryProvider {

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> immutableBuilder) {
        immutableBuilder.put(String.class, ((id, name, description, value, maxLength) -> new StringConfigEntry(id, name, description, value.toString(), maxLength)));
        immutableBuilder.put(Integer.class, ((id, name, description, value, maxLength) -> new IntegerConfigEntry(id, name, description, Integer.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Float.class, ((id, name, description, value, maxLength) -> new FloatConfigEntry(id, name, description, Float.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Boolean.class, ((id, name, description, value, maxLength) -> new BooleanConfigEntry(id, name, description, (boolean) value, maxLength)));
        immutableBuilder.put(String[].class, ((id, name, description, value, maxLength) -> new ListStringEntry(id, name, description, Arrays.asList((String[]) value), maxLength)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, JsonElement>> immutableBuilder) {
        immutableBuilder.put(ArrayList.class, (value) -> {
            JsonArray jsonArray = new JsonArray();
            for (Object valu : (ArrayList<?>) value) {
                Function<Object, JsonElement> factory = ConfigFactories.saveFactories.get(valu.getClass());
                if (factory != null) {
                    jsonArray.add(factory.apply(valu));
                }
                else {
                    jsonArray.add(new JsonPrimitive(valu));
                }
            }
            return jsonArray;
        });
    }
}
