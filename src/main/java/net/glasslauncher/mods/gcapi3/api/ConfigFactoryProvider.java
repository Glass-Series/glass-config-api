package net.glasslauncher.mods.gcapi3.api;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public interface ConfigFactoryProvider {

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the OctFunction are: id, name, description, field, parentObject, value, multiplayerSynced, maxLength.
     *                         Should return a class returning a config entry for your custom config type.
     */
    void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder);

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the Function are: value.
     *                         Should return a JsonElement containing the serialized value for your custom config type.
     */
    void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder);

    /**
     * Return custom factories for certain config class types.
     * @param immutableBuilder Arguments for the Function are: value.
     *                         Should return the class of the value used inside the ConfigEntry.
     */
    default void provideLoadTypeAdapterFactories(@SuppressWarnings("rawtypes") ImmutableMap.Builder<Type, Class> immutableBuilder) {

    }
}
