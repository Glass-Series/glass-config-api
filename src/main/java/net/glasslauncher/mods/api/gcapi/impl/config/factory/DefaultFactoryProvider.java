package net.glasslauncher.mods.api.gcapi.impl.config.factory;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.impl.ConfigFactories;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.BooleanConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.FloatConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.FloatListConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.IntegerConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.IntegerListConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.StringConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.StringListConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.Vec3fConfigEntry;
import net.minecraft.util.maths.Vec3f;
import uk.co.benjiweber.expressions.function.SexFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class DefaultFactoryProvider implements ConfigFactoryProvider {

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SexFunction<String, String, String, Field, Object, Integer, ConfigEntry<?>>> immutableBuilder) {
        immutableBuilder.put(String.class, ((id, name, description, parentField, value, maxLength) -> new StringConfigEntry(id, name, description, parentField, value.toString(), maxLength)));
        immutableBuilder.put(Integer.class, ((id, name, description, parentField, value, maxLength) -> new IntegerConfigEntry(id, name, description, parentField, Integer.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Float.class, ((id, name, description, parentField, value, maxLength) -> new FloatConfigEntry(id, name, description, parentField, Float.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Boolean.class, ((id, name, description, parentField, value, maxLength) -> new BooleanConfigEntry(id, name, description, parentField, (boolean) value)));
        immutableBuilder.put(String[].class, ((id, name, description, parentField, value, maxLength) -> new StringListConfigEntry(id, name, description, parentField, new ArrayList<>(Arrays.asList((String[]) value)), maxLength))); // the new ArrayList is required or it returns java.util.Arrays.ArrayList, which is fucking dumb.
        immutableBuilder.put(Integer[].class, ((id, name, description, parentField, value, maxLength) -> new IntegerListConfigEntry(id, name, description, parentField, new ArrayList<>(Arrays.asList((Integer[]) value)), maxLength)));
        immutableBuilder.put(Float[].class, ((id, name, description, parentField, value, maxLength) -> new FloatListConfigEntry(id, name, description, parentField, new ArrayList<>(Arrays.asList((Float[]) value)), maxLength)));
        immutableBuilder.put(Vec3f.class, ((id, name, description, parentField, value, maxLength) -> new Vec3fConfigEntry(id, name, description, parentField, (Vec3f) value)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, JsonElement>> immutableBuilder) {
        immutableBuilder.put(String.class, JsonPrimitive::new);
        immutableBuilder.put(Integer.class, JsonPrimitive::new);
        immutableBuilder.put(Float.class, JsonPrimitive::new);
        immutableBuilder.put(Boolean.class, JsonPrimitive::new);
        immutableBuilder.put(ArrayList.class, (value) -> {
            JsonArray jsonArray = new JsonArray();
            for (Object valu : (ArrayList) value) {
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
        immutableBuilder.put(Vec3f.class, (value) -> new JsonPrimitive(((Vec3f) value).x + "," + ((Vec3f) value).y + "," + ((Vec3f) value).z));
    }
}
