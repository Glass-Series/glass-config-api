package net.glasslauncher.mods.api.gcapi.impl;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigBase;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.impl.example.ExampleConfig;
import net.minecraft.util.io.CompoundTag;
import net.modificationstation.stationapi.api.util.ReflectionHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import uk.co.benjiweber.expressions.function.OctFunction;
import uk.co.benjiweber.expressions.function.SeptFunction;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class GlassConfigAPI implements PreLaunchEntrypoint {
    public static final ModContainer MOD_ID = FabricLoader.getInstance().getModContainer("gcapi").orElseThrow(RuntimeException::new);
    public static final HashMap<ModContainer, BiTuple<EntrypointContainer<Object>, ConfigCategory>> MOD_CONFIGS = new HashMap<>();
    private static boolean loaded = false;
    private static final Logger LOGGER = LogManager.getFormatterLogger("GCAPI");

    @Getter
    private static boolean serverConfLoaded = false;

    static {
        Configurator.setLevel("GCAPI", Level.INFO);
    }

    public static void loadServerConfig(String modID, String string) {
        serverConfLoaded = true;
        AtomicReference<ModContainer> mod = new AtomicReference<>();
        MOD_CONFIGS.keySet().forEach(modContainer -> {
            if (modContainer.getMetadata().getId().equals(modID)) {
                mod.set(modContainer);
            }
        });
        if (mod.get() != null) {
            BiTuple<EntrypointContainer<Object>, ConfigCategory> category = MOD_CONFIGS.get(mod.get());
            for (Field field : ReflectionHelper.getFieldsWithAnnotation(category.one().getEntrypoint().getClass(), GConfig.class)) {
                loadModConfig(category.one().getEntrypoint(), mod.get(), field, string);
            }
        }
    }

    public static void exportConfigsForServer(CompoundTag compoundTag) {
        for (ModContainer modContainer : MOD_CONFIGS.keySet()) {
            BiTuple<EntrypointContainer<Object>, ConfigCategory> entry = MOD_CONFIGS.get(modContainer);
            compoundTag.put(modContainer.getMetadata().getId(), saveConfig(entry.one(), entry.two()));
        }
    }

    @Override
    public void onPreLaunch() {
        loadConfigs();
    }

    public static void log(String message) {
        LOGGER.info(message);
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, message);
    }

    private static void loadConfigs() {
        if (loaded) {
            log(Level.WARN, "Tried to load configs a second time! Printing stacktrace and aborting!");
            new Exception("Stacktrace for duplicate loadConfigs call!").printStackTrace();
            return;
        }
        loaded = true;
        log("Loading config factories.");

        List<EntrypointContainer<ConfigFactoryProvider>> containers = FabricLoader.getInstance().getEntrypointContainers("gcapi:factory_provider", ConfigFactoryProvider.class);

        ImmutableMap.Builder<Type, OctFunction<String, String, String, Field, Object, Boolean, Object, Integer, ConfigEntry<?>>> loadImmutableBuilder = ImmutableMap.builder();
        containers.forEach((customConfigFactoryProviderEntrypointContainer -> customConfigFactoryProviderEntrypointContainer.getEntrypoint().provideLoadFactories(loadImmutableBuilder)));
        ConfigFactories.loadFactories = loadImmutableBuilder.build();
        log(ConfigFactories.loadFactories.size() + " config load factories loaded.");

        ImmutableMap.Builder<Type, Function<Object, JsonElement>> saveImmutableBuilder = ImmutableMap.builder();
        containers.forEach((customConfigFactoryProviderEntrypointContainer -> customConfigFactoryProviderEntrypointContainer.getEntrypoint().provideSaveFactories(saveImmutableBuilder)));
        ConfigFactories.saveFactories = saveImmutableBuilder.build();
        log(ConfigFactories.saveFactories.size() + " config save factories loaded.");

        log("Loading config event listeners.");
        EventStorage.loadListeners();
        log("Loaded config event listeners.");

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
            try {
                MOD_CONFIGS.put(entrypointContainer.getProvider(), BiTuple.of(entrypointContainer, null));
                for (Field field : ReflectionHelper.getFieldsWithAnnotation(entrypointContainer.getEntrypoint().getClass(), GConfig.class)) {
                    loadModConfig(entrypointContainer.getEntrypoint(), entrypointContainer.getProvider(), field, null);
                    saveConfig(entrypointContainer, MOD_CONFIGS.get(entrypointContainer.getProvider()).two());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                GlassConfigAPI.log(ExampleConfig.exampleConfigClass.asd2);
                GlassConfigAPI.log(ExampleConfig.exampleConfigClass.testConfig1);
            }
        }, 0, 1000);
    }

    public static void loadModConfig(Object rootConfigObject, ModContainer modContainer, Field configField, String jsonOverrideString) {
        log(rootConfigObject.getClass().getName());
        log(configField.getName());
        AtomicInteger totalReadCategories = new AtomicInteger();
        AtomicInteger totalReadFields = new AtomicInteger();
        try {
            configField.setAccessible(true);
            Object objField = configField.get(rootConfigObject);
            File modConfigFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), modContainer.getMetadata().getId() + "/" + configField.getAnnotation(GConfig.class).value() + ".json");
            JsonObject rootJsonObject;
            if (jsonOverrideString == null) {
                if (modConfigFile.exists()) {
                    rootJsonObject = Jankson.builder().build().load(modConfigFile);
                }
                else {
                    rootJsonObject = new JsonObject();
                }
            }
            else {
                log("Loading server config!");
                serverConfLoaded = true;
                rootJsonObject = Jankson.builder().build().load(jsonOverrideString);
            }
            log(Arrays.toString(rootJsonObject.keySet().toArray()));
            ConfigCategory configCategory = new ConfigCategory(modContainer.getMetadata().getId(), configField.getAnnotation(GConfig.class).visibleName(), null, configField, objField, configField.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), true);
            for (Field field : objField.getClass().getDeclaredFields()) {
                Object childObjField = field.get(objField);
                if (childObjField instanceof IsConfigCategory) {
                    JsonObject jsonCategory = rootJsonObject.getObject(field.getName());
                    if (jsonCategory == null) {
                        jsonCategory = new JsonObject();
                        rootJsonObject.put(field.getName(), jsonCategory);
                    }
                    ConfigCategory childCategory = new ConfigCategory(field.getName(), ((IsConfigCategory) childObjField).getVisibleName(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, configCategory.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), false);
                    configCategory.values.put(IsConfigCategory.class, childCategory);
                    readDeeper(objField, field, jsonCategory, childCategory, totalReadFields, totalReadCategories);
                }
                else {
                    if (!field.isAnnotationPresent(ConfigName.class)) {
                        throw new RuntimeException("Config value \"" + field.getClass().getName() + ";" + field.getName() + "\" has no ConfigName annotation!");
                    }
                    OctFunction<String, String, String, Field, Object, Boolean, Object, Integer, ConfigEntry<?>> function = ConfigFactories.loadFactories.get(field.getType());
                    if (function == null) {
                        throw new RuntimeException("Config value \"" + field.getClass().getName() + ";" + field.getName() + "\" has no config loader for it's type!");
                    }
                    field.setAccessible(true);
                    ConfigEntry<?> configEntry = function.apply(field.getName(), field.getAnnotation(ConfigName.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, configCategory.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), rootJsonObject.get(field.getType(), field.getName()) != null? rootJsonObject.get(field.getType(), field.getName()) : childObjField, field.isAnnotationPresent(MaxLength.class)? field.getAnnotation(MaxLength.class).value() : 32);
                    configCategory.values.put(field.getType(), configEntry);
                    field.set(objField, configEntry.value);
                    totalReadFields.getAndIncrement();
                }
            }
            MOD_CONFIGS.put(modContainer, BiTuple.of(MOD_CONFIGS.remove(modContainer).one(), configCategory));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log("Successfully read \"" + modContainer.getMetadata().getId() + "\"'s mod configs, reading " + totalReadCategories.get() + " categories, and " + totalReadFields.get() + " values.");
    }

    private static void readDeeper(Object rootConfigObject, Field configField, JsonObject rootJsonObject, ConfigCategory category, AtomicInteger totalReadFields, AtomicInteger totalReadCategories) throws IllegalAccessException {
        totalReadCategories.getAndIncrement();
        configField.setAccessible(true);
        Object objField = configField.get(rootConfigObject);

        for (Field field : objField.getClass().getDeclaredFields()) {
            Object childObjField = field.get(objField);
            if (childObjField instanceof IsConfigCategory) {
                JsonObject jsonCategory = rootJsonObject.getObject(field.getName());
                if (jsonCategory == null) {
                    jsonCategory = new JsonObject();
                    rootJsonObject.put(field.getName(), jsonCategory);
                }
                ConfigCategory childCategory = new ConfigCategory(field.getName(), ((IsConfigCategory) childObjField).getVisibleName(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, category.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), false);
                category.values.put(IsConfigCategory.class, childCategory);
                readDeeper(objField, field, jsonCategory, childCategory, totalReadFields, totalReadCategories);
            }
            else {
                if (!field.isAnnotationPresent(ConfigName.class)) {
                    throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no ConfigName annotation!");
                }
                OctFunction<String, String, String, Field, Object, Boolean, Object, Integer, ConfigEntry<?>> function = ConfigFactories.loadFactories.get(field.getType());
                if (function == null) {
                    throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no config loader for it's type!");
                }
                field.setAccessible(true);
                ConfigEntry<?> configEntry = function.apply(field.getName(), field.getAnnotation(ConfigName.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, category.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), rootJsonObject.get(field.getType(), field.getName()) != null? rootJsonObject.get(field.getType(), field.getName()) : childObjField, field.isAnnotationPresent(MaxLength.class)? field.getAnnotation(MaxLength.class).value() : 32);
                category.values.put(field.getType(), configEntry);
                field.set(objField, configEntry.value);
                totalReadFields.getAndIncrement();
            }
        }
    }

    public static String saveConfig(EntrypointContainer<Object> container, ConfigCategory category) {
        try {
            AtomicInteger readValues = new AtomicInteger();
            AtomicInteger readCategories = new AtomicInteger();
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), container.getProvider().getMetadata().getId() + "/" + category.parentField.getAnnotation(GConfig.class).value() + ".json");
            JsonObject newValues = new JsonObject();
            JsonObject serverExported = new JsonObject();

            for (ConfigBase entry : category.values.values()) {
                if (entry instanceof ConfigCategory) {
                    log(entry.parentField.getDeclaringClass().getName());
                    log(container.getEntrypoint().getClass().getName());
                    BiTuple<JsonObject, JsonObject> values = saveDeeper((ConfigCategory) entry, entry.parentField, readValues, readCategories);
                    newValues.put(entry.id, values.one());
                    serverExported.put(entry.id, values.two());
                    readCategories.getAndIncrement();
                } else if (entry instanceof ConfigEntry) {
                    Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                    if (configFactory == null) {
                        throw new RuntimeException("Config value \"" + entry.parentObject.getClass().getName() + ";" + entry.id + "\" has no config saver for it's type!");
                    }
                    JsonElement jsonElement = configFactory.apply(((ConfigEntry<?>) entry).value);
                    newValues.put(entry.id, jsonElement, entry.description);
                    if (entry.multiplayerSynced) {
                        serverExported.put(entry.id, jsonElement, entry.description);
                    }
                    entry.parentField.setAccessible(true);
                    ((ConfigEntry<?>) entry).saveToField();
                    readValues.getAndIncrement();
                } else {
                    throw new RuntimeException("What?! Config contains a non-serializable entry!");
                }
            }

            if (EventStorage.PRE_SAVE_LISTENERS.containsKey(container.getProvider().getMetadata().getId())) {
                EventStorage.PRE_SAVE_LISTENERS.get(container.getProvider().getMetadata().getId()).getEntrypoint().onPreConfigSaved(configFile.exists() ? Jankson.builder().build().load(configFile) : new JsonObject(), newValues);
            }

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }

            FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
            fileOutputStream.write(newValues.toJson(true, true).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            log("Successfully saved " + readCategories + " categories, containing " + readValues.get() + " values for " + container.getProvider().getMetadata().getName() + "(" + container.getProvider().getMetadata().getId() + ").");
            return serverExported.toJson();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static BiTuple<JsonObject, JsonObject> saveDeeper(ConfigCategory category, Field childField, AtomicInteger readValues, AtomicInteger readCategories) throws IllegalAccessException {
        JsonObject jsonObject = new JsonObject();
        JsonObject serverExported = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            childField.setAccessible(true);
            if (entry instanceof ConfigCategory) {
                BiTuple<JsonObject, JsonObject> values = saveDeeper((ConfigCategory) entry, entry.parentField, readValues, readCategories);
                jsonObject.put(entry.id, values.one());
                serverExported.put(entry.id, values.two());
                readCategories.getAndIncrement();
            }
            else if (entry instanceof ConfigEntry) {
                Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                if (configFactory == null) {
                    throw new RuntimeException("Config value \"" + entry.parentObject.getClass().getName() + ";" + entry.id + "\" has no config saver for it's type!");
                }
                JsonElement jsonElement = configFactory.apply(((ConfigEntry<?>) entry).value);
                jsonObject.put(entry.id, jsonElement, entry.description);
                if (entry.multiplayerSynced) {
                    serverExported.put(entry.id, jsonElement, entry.description);
                }
                entry.parentField.setAccessible(true);
                ((ConfigEntry<?>) entry).saveToField();
                readValues.getAndIncrement();
            }
            else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }
        return BiTuple.of(jsonObject, serverExported);
    }
}
