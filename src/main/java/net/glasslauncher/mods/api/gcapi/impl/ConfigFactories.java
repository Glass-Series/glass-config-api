package net.glasslauncher.mods.api.gcapi.impl;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import uk.co.benjiweber.expressions.function.QuadFunction;

import java.lang.reflect.Type;

public class ConfigFactories {

    public static ImmutableMap<Type, QuadFunction<String, String, String, Object, ConfigEntry<?>>> factories = null;
}
