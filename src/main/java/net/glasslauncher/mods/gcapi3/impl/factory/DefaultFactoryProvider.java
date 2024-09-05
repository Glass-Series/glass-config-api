package net.glasslauncher.mods.gcapi3.impl.factory;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.impl.SeptFunction;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.BooleanConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.FloatConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.FloatListConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.IntegerConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.IntegerListConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.StringConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.StringListConfigEntryHandler;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class DefaultFactoryProvider implements ConfigFactoryProvider {

    @SuppressWarnings("unchecked")
    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder) {
        immutableBuilder.put(String.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new StringConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, String.valueOf(value), String.valueOf(defaultValue))));
        immutableBuilder.put(Integer.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new IntegerConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, Integer.valueOf(String.valueOf(value)), Integer.valueOf(String.valueOf(defaultValue)))));
        immutableBuilder.put(Float.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new FloatConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, Float.valueOf(String.valueOf(value)), Float.valueOf(String.valueOf(defaultValue)))));
        immutableBuilder.put(Boolean.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new BooleanConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, (boolean) value, (boolean) defaultValue)));
        immutableBuilder.put(String[].class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new StringListConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, listOrArrayToArray(value, String[]::new, val -> val), (String[]) defaultValue)));
        immutableBuilder.put(Integer[].class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new IntegerListConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, listOrArrayToArray(value, Integer[]::new, Integer::parseInt), (Integer[]) defaultValue)));
        immutableBuilder.put(Float[].class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, value, defaultValue) -> new FloatListConfigEntryHandler(id, configEntry, parentField, parentObject, isMultiplayerSynced, listOrArrayToArray(value, Float[]::new, Float::parseFloat), (Float[]) defaultValue)));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(String.class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(Integer.class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(Float.class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(Boolean.class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(String[].class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(Integer[].class, DefaultFactoryProvider::justPass);
        immutableBuilder.put(Float[].class, DefaultFactoryProvider::justPass);
    }

    public static <T> T justPass(T object) {
        return object;
    }

    /**
     * This is a fucking mess. Why does simpleyaml not read to arraylists sanely?
     */
    public static <T> T[] listOrArrayToArray(Object object, IntFunction<T[]> type, Function<String, T> elementFixer) {
        if (object instanceof List<?> list) {
            return list.stream().map(val -> elementFixer.apply(val.toString())).toArray(type);
        }
        //noinspection unchecked // If this isn't right, we're fucked anyways
        return (T[]) object;
    }

    public static int enumOrOrdinalToOrdinal(Object enumOrOrdinal) {
        int enumOrdinal = 0;
        if(enumOrOrdinal instanceof Integer ordinal) {
            enumOrdinal = ordinal;
        }
        else if (enumOrOrdinal instanceof Enum<?> enumInst) {
            enumOrdinal = (enumInst).ordinal();
        }
        return enumOrdinal;
    }
}
