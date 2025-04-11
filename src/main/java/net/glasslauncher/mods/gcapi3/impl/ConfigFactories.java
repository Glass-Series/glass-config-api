package net.glasslauncher.mods.gcapi3.impl;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ConfigFactories {

    public static ImmutableMap<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> loadFactories = null;
    public static ImmutableMap<Type, Function<Object, Object>> saveFactories = null;

    @SuppressWarnings("rawtypes")
    public static ImmutableMap<Type, Class> loadTypeAdapterFactories = null;
}
