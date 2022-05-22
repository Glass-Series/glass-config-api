package net.glasslauncher.mods.api.gcapi.impl;

import blue.endless.jankson.JsonElement;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.OctFunction;

import java.lang.reflect.*;
import java.util.function.*;

public class ConfigFactories {

    public static ImmutableMap<Type, OctFunction<String, String, String, Field, Object, Boolean, Object, MaxLength, ConfigEntry<?>>> loadFactories = null;
    public static ImmutableMap<Type, Function<Object, JsonElement>> saveFactories = null;
}
