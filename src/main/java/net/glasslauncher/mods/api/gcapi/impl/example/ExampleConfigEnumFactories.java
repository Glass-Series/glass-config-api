package net.glasslauncher.mods.api.gcapi.impl.example;

import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.NonFunction;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.config.entry.EnumConfigEntry;

import java.lang.reflect.*;
import java.util.function.*;

public class ExampleConfigEnumFactories implements ConfigFactoryProvider {
    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, NonFunction<String, String, String, Field, Object, Boolean, Object, Object, MaxLength, ConfigEntry<?>>> immutableBuilder) {
        immutableBuilder.put(ExampleConfigEnum.class, ((id, name, description, parentField, parentObject, isMultiplayerSynced, enumOrdinal, defaultEnum, maxLength) -> new EnumConfigEntry<ExampleConfigEnum>(id, name, description, parentField, parentObject, isMultiplayerSynced, (Integer) enumOrdinal, ((ExampleConfigEnum) defaultEnum).ordinal(), ExampleConfigEnum.class)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, JsonElement>> immutableBuilder) {
        immutableBuilder.put(ExampleConfigEnum.class, enumEntry -> new JsonPrimitive(((ExampleConfigEnum) enumEntry).ordinal()));
    }

    @Override
    public void provideLoadTypeAdapterFactories(ImmutableMap.Builder<Type, Supplier<Class>> immutableBuilder) {
        immutableBuilder.put(ExampleConfigEnum.class, () -> Integer.class);
    }
}
