package net.glasslauncher.mods.api.gcapi.api;

import blue.endless.jankson.JsonElement;
import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.OctFunction;
import uk.co.benjiweber.expressions.function.SeptFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public interface ConfigFactoryProvider {

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the QuinFunction are: id, name, description, value, maxLength.
     *                         Should return a class returning a config entry for your custom config type.
     */
    void provideLoadFactories(ImmutableMap.Builder<Type, OctFunction<String, String, String, Field, Object, Boolean, Object, Integer, ConfigEntry<?>>> immutableBuilder);

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the QuinFunction are: id, name, description, value, maxLength.
     *                         Should return a JsonObject containing the config values for your custom config type.
     */
    void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, JsonElement>> immutableBuilder);
}
