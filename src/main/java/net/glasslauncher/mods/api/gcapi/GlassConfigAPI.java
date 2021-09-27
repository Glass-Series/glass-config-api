package net.glasslauncher.mods.api.gcapi;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;
import net.glasslauncher.mods.api.gcapi.impl.ConfigFactories;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.glasslauncher.mods.api.gcapi.screen.ConfigBase;
import net.glasslauncher.mods.api.gcapi.screen.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.RootScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.ownconfig.StringConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;
import uk.co.benjiweber.expressions.function.TriFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GlassConfigAPI {
    public static final ModContainer MOD_ID = FabricLoader.getInstance().getModContainer("gcapi").orElseThrow(RuntimeException::new);

    public static final HashMap<ModContainerEntrypoint, ConfigCategory> MOD_CONFIGS = new HashMap<>();

    public static void loadConfigs(ImmutableMap.Builder<String, Function<ScreenBase, ? extends ScreenBase>> builder) {
        ImmutableMap.Builder<Type, TriFunction<String, String, Object, ConfigEntry<?>>> map = ImmutableMap.builder();
        map.put(String.class, ((s, s2, o) -> new StringConfigEntry(s, s2, o.toString())));
        ConfigFactories.factories = map.build();
        AtomicInteger readFields = new AtomicInteger();

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID.getMetadata().getId(), HasConfigFields.class).forEach((objectEntrypointContainer -> {
            ModContainer mod = objectEntrypointContainer.getProvider();
            HasConfigFields config = objectEntrypointContainer.getEntrypoint();
            ModContainerEntrypoint modContainerEntrypoint = new ModContainerEntrypoint(mod, config);
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            ConfigCategory typeToValue = new ConfigCategory(config.getConfigPath(), null, HashMultimap.create());
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
                                JsonObject categoryObj = new JsonObject();
                                jsonObject.put(field.getName(), categoryObj);
                                typeToValue.values.put(key, readDeeper(field.get(null), field, categoryObj, readFields));
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
                            if (field.isAnnotationPresent(Comment.class)) {
                                String comment = field.getAnnotation(Comment.class).value();
                                jsonObject.setComment(field.getName(), comment);
                                typeToValue.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), comment, value));
                            }
                            else {
                                typeToValue.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), null, value));
                            }
                            readFields.getAndIncrement();
                        }
                    }
                    else {
                        throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                    }
                }
                MOD_CONFIGS.put(modContainerEntrypoint, typeToValue);


                FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
                fileOutputStream.write(jsonObject.toJson(true, true).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (Error | Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Successfully read " + MOD_CONFIGS.size() + " mod configs, reading " + readFields.get() + " values.");

        }));
        for (ModContainerEntrypoint mod : MOD_CONFIGS.keySet()) {
            builder.put(mod.mod.getMetadata().getId(), screenBase -> new RootScreenBuilder(screenBase, mod, MOD_CONFIGS.get(mod)));
        }
    }

    private static ConfigCategory readDeeper(Object categoryInstance, Field category, JsonObject jsonObject, AtomicInteger readFields) {
        try {
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            Comment categoryComment = category.getAnnotation(Comment.class);
            ConfigCategory typeToValue = new ConfigCategory(category.getName(), categoryComment == null? null : categoryComment.value(), HashMultimap.create());
            for (Field field : category.getType().getDeclaredFields()) {
                typeToField.put(field.getType(), field);
            }
            for (Class<?> key : typeToField.keySet()) {
                if (IsConfigCategory.class.isAssignableFrom(key)) {
                    typeToField.get(key).forEach((field) -> {
                        try {
                            JsonObject categoryObj = new JsonObject();
                            jsonObject.put(field.getName(), categoryObj);
                            typeToValue.values.put(key, readDeeper(field.get(categoryInstance), field, categoryObj, readFields));
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
                        if (field.isAnnotationPresent(Comment.class)) {
                            String comment = field.getAnnotation(Comment.class).value();
                            jsonObject.setComment(field.getName(), comment);
                            typeToValue.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), comment, value));
                        } else {
                            typeToValue.values.put(key, ConfigFactories.factories.get(key).apply(field.getName(), null, value));
                        }
                        readFields.getAndIncrement();
                    }
                } else {
                    throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                }
            }
            return typeToValue;
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
                jsonObject.put(entry.name, doConfigCategory((ConfigCategory) entry));
            } else if (entry instanceof ConfigEntry) {
                jsonObject.put(entry.name, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
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
                jsonObject.put(entry.name, doConfigCategory((ConfigCategory) entry));
            }
            else if (entry instanceof ConfigEntry) {
                jsonObject.put(entry.name, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
            }
            else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }
        return jsonObject;
    }
}
