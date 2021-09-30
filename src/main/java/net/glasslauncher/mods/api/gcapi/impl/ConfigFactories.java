package net.glasslauncher.mods.api.gcapi.impl;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.lang.reflect.Type;

public class ConfigFactories {

    public static ImmutableMap<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> factories = null;
}
