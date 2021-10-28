package net.glasslauncher.mods.api.gcapi.impl;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.api.ConfigName;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.api.IsConfigCategory;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigBase;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class GlassConfigAPI implements PreLaunchEntrypoint {
    public static final ModContainer MOD_ID = FabricLoader.getInstance().getModContainer("gcapi").orElseThrow(RuntimeException::new);
    public static final HashMap<ModContainerEntrypoint, ConfigCategory> MOD_CONFIGS = new HashMap<>();
    private static boolean loaded = false;
    private static final Logger LOGGER = LogManager.getFormatterLogger("GCAPI");

    static {
        Configurator.setLevel("GCAPI", Level.INFO);
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

        ImmutableMap.Builder<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> loadImmutableBuilder = ImmutableMap.builder();
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

        AtomicInteger totalReadFields = new AtomicInteger();
        AtomicInteger totalReadCategories = new AtomicInteger();

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID.getMetadata().getId(), HasConfigFields.class).forEach((objectEntrypointContainer -> {
            AtomicInteger readValues = new AtomicInteger();
            AtomicInteger readCategories = new AtomicInteger();
            ModContainer mod = objectEntrypointContainer.getProvider();
            HasConfigFields config = objectEntrypointContainer.getEntrypoint();
            ModContainerEntrypoint modContainerEntrypoint = new ModContainerEntrypoint(mod, config);
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            ConfigCategory category = new ConfigCategory(config.getConfigPath(), config.getVisibleName(), objectEntrypointContainer.getEntrypoint().getVisibleName(), HashMultimap.create(), true);
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
                JsonObject savedValues = Jankson.builder().build().load(configFile);
                for (Class<?> key : typeToField.keySet()) {
                    if (IsConfigCategory.class.isAssignableFrom(key)) {
                        typeToField.get(key).forEach((field) -> {
                            try {
                                JsonObject categoryObj = (JsonObject) savedValues.get(field.getName());
                                if (categoryObj == null) {
                                    categoryObj = new JsonObject();
                                    savedValues.put(field.getName(), categoryObj);
                                }
                                category.values.put(key, readDeeper(null, field, categoryObj, readValues, readCategories));
                                readCategories.incrementAndGet();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    else if (ConfigFactories.loadFactories.containsKey(key)) {
                        for (Field field : typeToField.get(key)) {
                            Object entry = savedValues.get(key, field.getName());
                            field.setAccessible(true);
                            Object value = entry == null ? field.get(config) : entry;
                            field.set(config, value);
                            Function<Object, JsonElement> factory = ConfigFactories.saveFactories.get(field.getType());
                            JsonElement jsonEntry = factory == null? new JsonPrimitive(value) : factory.apply(value);
                            savedValues.put(field.getName(), jsonEntry);
                            MaxLength maxLengthAnnotation = field.getAnnotation(MaxLength.class);
                            try {
                                category.values.put(key, ConfigFactories.loadFactories.get(key).apply(field.getName(), field.getAnnotation(ConfigName.class).value(), savedValues.getComment(field.getName()), value, maxLengthAnnotation != null? maxLengthAnnotation.value() : 32));
                            } catch (Exception e) {
                                throw new RuntimeException("Annotate your config entries with '@ConfigName(\"myname\")'!", e);
                            }
                            readValues.getAndIncrement();
                        }
                    }
                    else {
                        throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                    }
                }
                MOD_CONFIGS.put(modContainerEntrypoint, category);
                totalReadFields.addAndGet(readValues.get());
                totalReadCategories.addAndGet(readCategories.get());

                if (EventStorage.POST_LOAD_LISTENERS.containsKey(mod.getMetadata().getId())) {
                    EventStorage.POST_LOAD_LISTENERS.get(mod.getMetadata().getId()).getEntrypoint().PostConfigLoaded();
                }

                log("Successfully read " + readCategories + " categories, containing " + readValues.get() + " values for " + mod.getMetadata().getName() + "(" + mod.getMetadata().getId() + ").");

            } catch (Error | Exception e) {
                throw new RuntimeException(e);
            }

            log("Successfully read " + MOD_CONFIGS.size() + " mod configs, reading " + totalReadFields.get() + " values.");
            saveConfigs(modContainerEntrypoint);

        }));
    }

    private static ConfigCategory readDeeper(Object categoryInstance, Field categoryField, JsonObject savedValues, AtomicInteger readFields, AtomicInteger readCategories) {
        try {
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            Comment categoryComment = categoryField.getAnnotation(Comment.class);
            ConfigCategory category = new ConfigCategory(categoryField.getName(), ((IsConfigCategory) categoryField.get(categoryInstance)).getVisibleName(), categoryComment == null? null : categoryComment.value(), HashMultimap.create(), false);
            for (Field field : categoryField.getType().getDeclaredFields()) {
                typeToField.put(field.getType(), field);
            }
            for (Class<?> key : typeToField.keySet()) {
                if (IsConfigCategory.class.isAssignableFrom(key)) {
                    typeToField.get(key).forEach((field) -> {
                        try {
                            JsonObject categoryObj = (JsonObject) savedValues.get(field.getName());
                            if (categoryObj == null) {
                                categoryObj = new JsonObject();
                                savedValues.put(field.getName(), categoryObj);
                            }
                            category.values.put(key, readDeeper(categoryField.get(categoryInstance), field, categoryObj, readFields, readCategories));
                            readCategories.incrementAndGet();
                        } catch (Exception e) {
                            throw new RuntimeException(e);

                        }
                    });
                } else if (ConfigFactories.loadFactories.containsKey(key)) {
                    for (Field field : typeToField.get(key)) {
                        Object entry = savedValues.get(key, field.getName());
                        field.setAccessible(true);
                        Object value = entry == null ? field.get(categoryField.get(categoryInstance)) : entry;
                        field.set(categoryField.get(categoryInstance), value);

                        Comment comment = field.getAnnotation(Comment.class);
                        MaxLength maxLengthAnnotation = field.getAnnotation(MaxLength.class);
                        try {
                            category.values.put(key, ConfigFactories.loadFactories.get(key).apply(field.getName(), field.getAnnotation(ConfigName.class).value(), comment != null? comment.value() : null, value, maxLengthAnnotation != null? maxLengthAnnotation.value() : 32));
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
        AtomicInteger readValues = new AtomicInteger();
        AtomicInteger readCategories = new AtomicInteger();
        ConfigCategory category = MOD_CONFIGS.get(mod);
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), mod.mod.getMetadata().getId() + "/" + mod.entrypoint.getConfigPath() + ".json");
        JsonObject newValues = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            if (entry instanceof ConfigCategory) {
                newValues.put(entry.id, saveDeeper((ConfigCategory) entry, readValues, readCategories));
                readCategories.getAndIncrement();
            }
            else if (entry instanceof ConfigEntry) {
                Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                if (configFactory != null) {
                    newValues.put(entry.id, configFactory.apply(((ConfigEntry<?>) entry).value), entry.description);
                }
                else {
                    newValues.put(entry.id, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
                }
                readValues.getAndIncrement();
            } else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }

        try {
            if (EventStorage.PRE_SAVE_LISTENERS.containsKey(mod.mod.getMetadata().getId())) {
                EventStorage.PRE_SAVE_LISTENERS.get(mod.mod.getMetadata().getId()).getEntrypoint().onPreConfigSaved(configFile.exists()? Jankson.builder().build().load(configFile) : new JsonObject(), newValues);
            }

            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }

            FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
            fileOutputStream.write(newValues.toJson(true, true).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            log("Successfully saved " + readCategories + " categories, containing " + readValues.get() + " values for " + mod.mod.getMetadata().getName() + "(" + mod.mod.getMetadata().getId() + ").");
        } catch (IOException | SyntaxError e) {
            throw new RuntimeException(e);
        }
    }

    private static JsonObject saveDeeper(ConfigCategory category, AtomicInteger readValues, AtomicInteger readCategories) {
        JsonObject jsonObject = new JsonObject();

        for (ConfigBase entry : category.values.values()) {
            if (entry instanceof ConfigCategory) {
                jsonObject.put(entry.id, saveDeeper((ConfigCategory) entry, readValues, readCategories));
                readCategories.getAndIncrement();
            }
            else if (entry instanceof ConfigEntry) {
                Function<Object, JsonElement> configFactory = ConfigFactories.saveFactories.get(((ConfigEntry<?>) entry).value.getClass());
                if (configFactory != null) {
                    jsonObject.put(entry.id, configFactory.apply(((ConfigEntry<?>) entry).value), entry.description);
                }
                else {
                    jsonObject.put(entry.id, new JsonPrimitive(((ConfigEntry<?>) entry).value), entry.description);
                }
                readValues.getAndIncrement();
            }
            else {
                throw new RuntimeException("What?! Config contains a non-serializable entry!");
            }
        }
        return jsonObject;
    }
}
