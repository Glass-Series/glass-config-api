package net.glasslauncher.mods.api.gcapi.api;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.lang.reflect.Type;

public interface ConfigFactoryProvider {

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the QuinFunction are: id, name, description, value, maxLength.
     *                         Should return a class returning a config entry for your custom config type.
     */
    void provideFactories(ImmutableMap.Builder<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> immutableBuilder);
}
