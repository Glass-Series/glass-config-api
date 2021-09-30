package net.glasslauncher.mods.api.gcapi.impl;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigBase;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlassConfigAPI implements PreLaunchEntrypoint {
    public static final ModContainer MOD_ID = FabricLoader.getInstance().getModContainer("gcapi").orElseThrow(RuntimeException::new);
    public static final HashMap<ModContainerEntrypoint, ConfigCategory> MOD_CONFIGS = new HashMap<>();
    private static boolean loaded = false;

    @Override
    public void onPreLaunch() {
        loadConfigs();
    }

    public static void log(String message) {
        log("INFO", message);
    }

    public static void log(String level, String message) {
        System.out.println("[" + level + "]<GCAPI>: " + message);

    }

    private static void loadConfigs() {
        if (loaded) {
            log("WARN", "Tried to load configs a second time! Printing stacktrace and aborting!");
            new Exception("Stacktrace for duplicate loadConfigs call!").printStackTrace();
            return;
        }
        loaded = true;
        log("Loading config factories.");
        ImmutableMap.Builder<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> immutableBuilder = ImmutableMap.builder();
        FabricLoader.getInstance().getEntrypointContainers("gcapi:factory_provider", ConfigFactoryProvider.class).forEach((customConfigFactoryProviderEntrypointContainer -> customConfigFactoryProviderEntrypointContainer.getEntrypoint().provideFactories(immutableBuilder)));
        ConfigFactories.factories = immutableBuilder.build();
        log(ConfigFactories.factories.size() + " config factories loaded.");

        AtomicInteger readFields = new AtomicInteger();

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID.getMetadata().getId(), HasConfigFields.class).forEach((objectEntrypointContainer -> {
            ModContainer mod = objectEntrypointContainer.getProvider();
            HasConfigFields config = objectEntrypointContainer.getEntrypoint();
            ModContainerEntrypoint modContainerEntrypoint = new ModContainerEntrypoint(mod, config);
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            ConfigCategory category = new ConfigCategory(config.getConfigPath(), config.getVisibleName(), objectEntrypointContainer.getEntrypoint().getVisibleName(), HashMultimap.create());
            try {
                File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), mod.getMetadata().getId() + "/" + config.getConfigPath() + ".json");
                if (!configFile.exists()) {
                    if (configFile.getParentFile().mkdirs() && configFile.createNewFile()) {
                        FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
                        fileOutputStream.write("{}".getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }

                for (Field field : config.getClass().getDeclaredFields()) {
                    typeToField.put(field.getType(), field);
                }
                JsonObject jsonObject = Jankson.builder().build().load(configFile);
                for (Class<?> key : typeToField.keySet()) {
                    if (IsConfigCategory.class.isAssignableFrom(key)) {
                        typeToField.get(key).forEach((field) -> {
                            try {
                                JsonObject categoryObj = (JsonObject) jsonObject.get(field.getName());
                                if (categoryObj == null) {
                                    categoryObj = new JsonObject();
                                    jsonObject.put(field.getName(), categoryObj);
                                }
                                category.values.put(key, readDeeper(field.get(null), field, categoryObj, readFields));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else if (ConfigFactories.factories.containsKey(key)) {
                        for (Field field : typeToField.get(key)) {
                            Object entry = jsonObject.get(key, field.getName());
                            field.setAccessible(true);
                            Object value = entry == null ? field.get(config) : entry;
                            field.set(config, value);
                            JsonPrimitive jsonEntry = new JsonPrimitive(value);
                            jsonObject.put(field.getName(), jsonEntry);
                            Comment comment = field.getAnnotation(Comment.class);
                            if (comment != null) {
                                jsonObject.setComment(field.getName(), comment.value());
                            }
                            MaxLength maxLengthAnnotation = field.getAnnotation(MaxLength.class);
                            try {
                                category.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), field.getAnnotation(ConfigName.class).value(), comment != null? comment.value() : null, value, maxLengthAnnotation != null? maxLengthAnnotation.value() : 32));
                            } catch (Exception e) {
                                throw new RuntimeException("Annotate your config entries with '@ConfigName(\"myname\")'!", e);
                            }
                            readFields.getAndIncrement();
                        }
                    }
                    else {
                        throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                    }
                }
                MOD_CONFIGS.put(modContainerEntrypoint, category);


                FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
                fileOutputStream.write(jsonObject.toJson(true, true).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (Error | Exception e) {
                throw new RuntimeException(e);
            }
            log("Successfully read " + MOD_CONFIGS.size() + " mod configs, reading " + readFields.get() + " values.");

        }));
    }

    private static ConfigCategory readDeeper(Object categoryInstance, Field categoryField, JsonObject jsonObject, AtomicInteger readFields) {
        try {
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            Comment categoryComment = categoryField.getAnnotation(Comment.class);
            ConfigCategory category = new ConfigCategory(categoryField.getName(), ((IsConfigCategory) categoryField.get(categoryInstance)).getVisibleName(), categoryComment == null? null : categoryComment.value(), HashMultimap.create());
            for (Field field : categoryField.getType().getDeclaredFields()) {
                typeToField.put(field.getType(), field);
            }
            for (Class<?> key : typeToField.keySet()) {
                if (IsConfigCategory.class.isAssignableFrom(key)) {
                    typeToField.get(key).forEach((field) -> {
                        try {
                            JsonObject categoryObj = (JsonObject) jsonObject.get(field.getName());
                            if (categoryObj == null) {
                                categoryObj = new JsonObject();
                                jsonObject.put(field.getName(), categoryObj);
                            }
                            category.values.put(key, readDeeper(field.get(null), field, categoryObj, readFields));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (ConfigFactories.factories.containsKey(key)) {
                    for (Field field : typeToField.get(key)) {
                        Object entry = jsonObject.get(key, field.getName());
                        field.setAccessible(true);
                        Object value = entry == null ? field.get(categoryInstance) : entry;
                        field.set(categoryInstance, value);
                        JsonPrimitive jsonEntry = new JsonPrimitive(value);
                        jsonObject.put(field.getName(), jsonEntry);
                        Comment comment = field.getAnnotation(Comment.class);
                        if (comment != null) {
                            jsonObject.setComment(field.getName(), comment.value());
                        }
                        MaxLength maxLengthAnnotation = field.getAnnotation(MaxLength.class);
                        try {
                            category.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), field.getAnnotation(ConfigName.class).value(), comment != null? comment.value() : null, value, maxLengthAnnotation != null? maxLengthAnnotation.value() : 32));
                        } catch (Exception e) {
                            throw new RuntimeException("Annotate your config entries with '@ConfigName(\"myname\")'!", e);
                        }
                        readFields.getAndIncrement();
                    }
                } else {
                    throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                }
            }
            return category;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveConfigs(ModContainerEntrypoint mod) {
        ConfigCategory category = MOD_CONFIGS.get(mod);
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), mod.mod.getMetadata().getId() + "/" + mod.entrypoint.getConfigPath() + ".json");
        JsonObject jsonObject = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            if (entry instanceof ConfigCategory) {
                jsonObject.put(entry.id, doConfigCategory((ConfigCategory) entry));
            } else if (entry instanceof ConfigEntry) {
                jsonObject.put(entry.id, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
            } else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }

        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                    configFile.createNewFile();
            }
            FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
            fileOutputStream.write(jsonObject.toJson(true, true).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject doConfigCategory(ConfigCategory category) {
        JsonObject jsonObject = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            if (entry instanceof ConfigCategory) {
                jsonObject.put(entry.id, doConfigCategory((ConfigCategory) entry));
            }
            else if (entry instanceof ConfigEntry) {
                jsonObject.put(entry.id, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
            }
            else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }
        return jsonObject;
    }
}
