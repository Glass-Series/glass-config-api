package net.glasslauncher.mods.api.gcapi.impl;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.glasslauncher.mods.api.gcapi.api.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.api.MultiplayerSynced;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigBase;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.nbt.NbtCompound;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.util.ReflectionHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import uk.co.benjiweber.expressions.function.OctFunction;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

/**
 * Do not use this class directly in your code.
 * This class changes a lot between updates, and should never ever be used by a mod using GCAPI, as there are update-safe wrappers for most of this class' functionality inside other classes.
 */
@SuppressWarnings("DeprecatedIsStillUsed") // shush, I just don't want others using this class without getting yelled at.
@Deprecated
public class GCCore implements PreLaunchEntrypoint {
    public static final ModContainer NAMESPACE = FabricLoader.getInstance().getModContainer("gcapi").orElseThrow(RuntimeException::new);
    public static final HashMap<Identifier, BiTuple<EntrypointContainer<Object>, net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory>> MOD_CONFIGS = new HashMap<>();
    private static boolean loaded = false;
    private static final Logger LOGGER = LogManager.getFormatterLogger("GCAPI");

    private static final Supplier<MaxLength> MAX_LENGTH_SUPPLIER = () -> new MaxLength() {
        @Override
        public Class<? extends Annotation> annotationType() {
            return MaxLength.class;
        }

        @Override
        public int value() {
            return 32;
        }

        @Override
        public int arrayValue() {
            return -1;
        }

        @Override
        public boolean fixedArray() {
            return false;
        }
    };

    static {
        Configurator.setLevel("GCAPI", Level.INFO);
    }

    public static void loadServerConfig(String modID, String string) {
        AtomicReference<Identifier> mod = new AtomicReference<>();
        MOD_CONFIGS.keySet().forEach(modContainer -> {
            if (modContainer.toString().equals(modID)) {
                mod.set(modContainer);
            }
        });
        if (mod.get() != null) {
            BiTuple<EntrypointContainer<Object>, net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory> category = MOD_CONFIGS.get(mod.get());
            saveConfig(category.one(), category.two());
            loadModConfig(category.one().getEntrypoint(), category.one().getProvider(), category.two().parentField, mod.get(), string);
        }
    }

    public static void exportConfigsForServer(NbtCompound nbtCompound) {
        for (Identifier modContainer : MOD_CONFIGS.keySet()) {
            BiTuple<EntrypointContainer<Object>, net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory> entry = MOD_CONFIGS.get(modContainer);
            nbtCompound.putString(modContainer.toString(), saveConfig(entry.one(), entry.two()));
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
        log("Loading config factories.");

        List<EntrypointContainer<ConfigFactoryProvider>> containers = FabricLoader.getInstance().getEntrypointContainers("gcapi:factory_provider", ConfigFactoryProvider.class);

        ImmutableMap.Builder<Type, OctFunction<String, String, String, Field, Object, Boolean, Object, MaxLength, ConfigEntry<?>>> loadImmutableBuilder = ImmutableMap.builder();
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

        FabricLoader.getInstance().getEntrypointContainers(NAMESPACE.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
            try {
                for (Field field : ReflectionHelper.getFieldsWithAnnotation(entrypointContainer.getEntrypoint().getClass(), GConfig.class)) {
                    Identifier configID = Identifier.of(entrypointContainer.getProvider().getMetadata().getId() + ":" + field.getAnnotation(GConfig.class).value());
                    MOD_CONFIGS.put(configID, BiTuple.of(entrypointContainer, null));
                    loadModConfig(entrypointContainer.getEntrypoint(), entrypointContainer.getProvider(), field, configID, null);
                    saveConfig(entrypointContainer, MOD_CONFIGS.get(configID).two());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        loaded = true;
    }

    public static void loadModConfig(Object rootConfigObject, ModContainer modContainer, Field configField, Identifier configID, String jsonOverrideString) {
        log(rootConfigObject.getClass().getName());
        log(configField.getName());
        AtomicInteger totalReadCategories = new AtomicInteger();
        AtomicInteger totalReadFields = new AtomicInteger();
        boolean isMultiplayer = false;
        boolean forceNotMultiplayer = false;
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
                rootJsonObject = Jankson.builder().build().load(jsonOverrideString);
                forceNotMultiplayer = rootJsonObject.getBoolean("forceNotMultiplayer", false);
                if (!forceNotMultiplayer) {
                    isMultiplayer = true;
                    log("Loading server config for " + modContainer.getMetadata().getId() + "!");
                }
                else {
                    log("Loading forced mod config for " + modContainer.getMetadata().getId() + "!");
                }
            }
            log(Arrays.toString(rootJsonObject.keySet().toArray()));
            net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory configCategory = new net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory(modContainer.getMetadata().getId(), configField.getAnnotation(GConfig.class).visibleName(), null, configField, objField, configField.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), true);
            for (Field field : objField.getClass().getDeclaredFields()) {
                Object childObjField = field.get(objField);
                if (field.isAnnotationPresent(ConfigCategory.class)) {
                    JsonObject jsonCategory = rootJsonObject.getObject(field.getName());
                    if (jsonCategory == null) {
                        jsonCategory = new JsonObject();
                        rootJsonObject.put(field.getName(), jsonCategory);
                    }
                    net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory childCategory = new net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory(field.getName(), field.getAnnotation(ConfigCategory.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, configCategory.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), false);
                    configCategory.values.put(ConfigCategory.class, childCategory);
                    readDeeper(objField, field, jsonCategory, childCategory, totalReadFields, totalReadCategories, isMultiplayer, forceNotMultiplayer);
                }
                else {
                    if (!field.isAnnotationPresent(ConfigName.class)) {
                        throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no ConfigName annotation!");
                    }
                    OctFunction<String, String, String, Field, Object, Boolean, Object, MaxLength, ConfigEntry<?>> function = ConfigFactories.loadFactories.get(field.getType());
                    if (function == null) {
                        throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no config loader for it's type!");
                    }
                    field.setAccessible(true);
                    ConfigEntry<?> configEntry = function.apply(field.getName(), field.getAnnotation(ConfigName.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, configCategory.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), rootJsonObject.get(field.getType(), field.getName()) != null? rootJsonObject.get(field.getType(), field.getName()) : childObjField, field.isAnnotationPresent(MaxLength.class)? field.getAnnotation(MaxLength.class) : MAX_LENGTH_SUPPLIER.get());
                    //noinspection ConstantConditions uh, no.
                    configEntry.multiplayerLoaded = isMultiplayer && configEntry.multiplayerSynced && !forceNotMultiplayer;
                    configCategory.values.put(field.getType(), configEntry);
                    field.set(objField, configEntry.value);
                    totalReadFields.getAndIncrement();
                }
            }
            if (!loaded) {
                MOD_CONFIGS.put(configID, BiTuple.of(MOD_CONFIGS.remove(configID).one(), configCategory));
            } else {
                MOD_CONFIGS.get(configID).two().values = configCategory.values;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log("Successfully read \"" + modContainer.getMetadata().getId() + "\"'s mod configs, reading " + totalReadCategories.get() + " categories, and " + totalReadFields.get() + " values.");
    }

    private static void readDeeper(Object rootConfigObject, Field configField, JsonObject rootJsonObject, net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory category, AtomicInteger totalReadFields, AtomicInteger totalReadCategories, boolean isMultiplayer, boolean forceNotMultiplayer) throws IllegalAccessException {
        totalReadCategories.getAndIncrement();
        configField.setAccessible(true);
        Object objField = configField.get(rootConfigObject);

        for (Field field : objField.getClass().getDeclaredFields()) {
            Object childObjField = field.get(objField);
            if (field.isAnnotationPresent(ConfigCategory.class)) {
                JsonObject jsonCategory = rootJsonObject.getObject(field.getName());
                if (jsonCategory == null) {
                    jsonCategory = new JsonObject();
                    rootJsonObject.put(field.getName(), jsonCategory);
                }
                net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory childCategory = new net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory(field.getName(), field.getAnnotation(ConfigCategory.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, category.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), HashMultimap.create(), false);
                category.values.put(ConfigCategory.class, childCategory);
                readDeeper(objField, field, jsonCategory, childCategory, totalReadFields, totalReadCategories, isMultiplayer, forceNotMultiplayer);
            }
            else {
                if (!field.isAnnotationPresent(ConfigName.class)) {
                    throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no ConfigName annotation!");
                }
                OctFunction<String, String, String, Field, Object, Boolean, Object, MaxLength, ConfigEntry<?>> function = ConfigFactories.loadFactories.get(field.getType());
                if (function == null) {
                    throw new RuntimeException("Config value \"" + field.getType().getName() + ";" + field.getName() + "\" has no config loader for it's type!");
                }
                field.setAccessible(true);
                ConfigEntry<?> configEntry = function.apply(field.getName(), field.getAnnotation(ConfigName.class).value(), field.isAnnotationPresent(Comment.class)? field.getAnnotation(Comment.class).value() : null, field, objField, category.multiplayerSynced || field.isAnnotationPresent(MultiplayerSynced.class), rootJsonObject.get(field.getType(), field.getName()) != null? rootJsonObject.get(field.getType(), field.getName()) : childObjField, field.isAnnotationPresent(MaxLength.class)? field.getAnnotation(MaxLength.class) : MAX_LENGTH_SUPPLIER.get());
                configEntry.multiplayerLoaded = isMultiplayer && configEntry.multiplayerSynced && !forceNotMultiplayer;
                category.values.put(field.getType(), configEntry);
                field.set(objField, configEntry.value);
                totalReadFields.getAndIncrement();
            }
        }
    }

    public static String saveConfig(EntrypointContainer<Object> container, net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory category) {
        try {
            AtomicInteger readValues = new AtomicInteger();
            AtomicInteger readCategories = new AtomicInteger();
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), container.getProvider().getMetadata().getId() + "/" + category.parentField.getAnnotation(GConfig.class).value() + ".json");
            JsonObject newValues;
            if (configFile.exists()) {
                newValues = Jankson.builder().build().load(configFile);
            }
            else {
                newValues = new JsonObject();
            }
            JsonObject serverExported = new JsonObject();

            for (ConfigBase entry : category.values.values()) {
                if (entry instanceof net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory) {
                    log(entry.parentField.getDeclaringClass().getName());
                    log(container.getEntrypoint().getClass().getName());
                    BiTuple<JsonObject, JsonObject> values = saveDeeper((net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory) entry, entry.parentField, readValues, readCategories);
                    newValues.put(entry.id, values.one());
                    serverExported.put(entry.id, values.two());
                    readCategories.getAndIncrement();
                } else if (entry instanceof ConfigEntry) {
                    Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                    if (configFactory == null) {
                        throw new RuntimeException("Config value \"" + entry.parentObject.getClass().getName() + ";" + entry.id + "\" has no config saver for it's type!");
                    }
                    JsonElement jsonElement = configFactory.apply(((ConfigEntry<?>) entry).value);
                    if (!((ConfigEntry<?>) entry).multiplayerLoaded) {
                        newValues.put(entry.id, jsonElement, entry.description);
                    }
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
                //noinspection ResultOfMethodCallIgnored
                configFile.getParentFile().mkdirs();
                //noinspection ResultOfMethodCallIgnored
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

    private static BiTuple<JsonObject, JsonObject> saveDeeper(net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory category, Field childField, AtomicInteger readValues, AtomicInteger readCategories) throws IllegalAccessException {
        JsonObject newValues = new JsonObject();
        JsonObject serverExported = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            childField.setAccessible(true);
            if (entry instanceof net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory) {
                BiTuple<JsonObject, JsonObject> values = saveDeeper((net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory) entry, entry.parentField, readValues, readCategories);
                newValues.put(entry.id, values.one());
                serverExported.put(entry.id, values.two());
                readCategories.getAndIncrement();
            }
            else if (entry instanceof ConfigEntry) {
                Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                if (configFactory == null) {
                    throw new RuntimeException("Config value \"" + entry.parentObject.getClass().getName() + ";" + entry.id + "\" has no config saver for it's type!");
                }
                JsonElement jsonElement = configFactory.apply(((ConfigEntry<?>) entry).value);
                if (!((ConfigEntry<?>) entry).multiplayerLoaded) {
                    newValues.put(entry.id, jsonElement, entry.description);
                }
                if (entry.multiplayerSynced) {
                    serverExported.put(entry.id, jsonElement, entry.description);
                }
                ((ConfigEntry<?>) entry).saveToField();
                readValues.getAndIncrement();
            }
            else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }
        return BiTuple.of(newValues, serverExported);
    }
}
