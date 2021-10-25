package net.glasslauncher.mods.api.gcapi.impl;

import blue.endless.jankson.JsonElement;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.lang.reflect.Type;
import java.util.function.Function;

public class ConfigFactories {

    public static ImmutableMap<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> loadFactories = null;
    public static ImmutableMap<Type, Function<Object, JsonElement>> saveFactories = null;
}
