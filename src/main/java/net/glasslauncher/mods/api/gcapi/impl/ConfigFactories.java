package net.glasslauncher.mods.api.gcapi.impl;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import uk.co.benjiweber.expressions.function.TriFunction;

import java.lang.reflect.Type;

public class ConfigFactories {

    public static ImmutableMap<Type, TriFunction<String, String, Object, ConfigEntry<?>>> factories = null;
}
