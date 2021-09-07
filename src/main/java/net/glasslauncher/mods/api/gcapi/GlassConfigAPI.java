package net.glasslauncher.mods.api.gcapi;


import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.github.minecraftcursedlegacy.api.Mod;
import io.github.prospector.modmenu.ModMenu;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.api.HasConfigFields;
import net.glasslauncher.mods.api.gcapi.impl.ConfigFactories;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.ScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.ownconfig.StringConfigEntry;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.mine_diver.unsafeevents.listener.ListenerPriority;
import net.minecraft.client.gui.screen.ScreenBase;
import net.modificationstation.stationapi.api.StationAPI;
import net.modificationstation.stationapi.api.event.mod.PreInitEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.impl.util.ReflectionHelper;
import uk.co.benjiweber.expressions.function.TriFunction;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.function.Function;

public class GlassConfigAPI {
    @Entrypoint.ModID
    public static final ModID MOD_ID = Null.get();

    public static final Multimap<ModID, Multimap<Class<?>, ConfigEntry<?>>> MOD_CONFIGS = HashMultimap.create();

    public static void loadConfigs(ImmutableMap.Builder<String, Function<ScreenBase, ? extends ScreenBase>> builder) {
        ImmutableMap.Builder<Type, TriFunction<String, String, Object, ConfigEntry<?>>> map = ImmutableMap.builder();
        map.put(String.class, ((s, s2, o) -> new StringConfigEntry(s, s2, o.toString())));
        ConfigFactories.factories = map.build();

        FabricLoader.getInstance().getEntrypointContainers(MOD_ID.toString(), HasConfigFields.class).forEach((objectEntrypointContainer -> {
            ModContainer mod = objectEntrypointContainer.getProvider();
            HasConfigFields config = objectEntrypointContainer.getEntrypoint();
            Multimap<Class<?>, Field> typeToField = HashMultimap.create();
            Multimap<Class<?>, ConfigEntry<?>> typeToValue = HashMultimap.create();
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
                    if (ConfigFactories.factories.containsKey(key)) {
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
                                typeToValue.put(key, ConfigFactories.factories.get(key).apply(field.getName(), comment, value));
                            }
                            else {
                                typeToValue.put(key, ConfigFactories.factories.get(key).apply(field.getName(), null, value));
                            }
                        }
                    }
                    else {
                        throw new RuntimeException("Data factory not found for \"" + key.getName() + "\"!");
                    }
                }
                MOD_CONFIGS.put(ModID.of(mod), typeToValue);

                FileOutputStream fileOutputStream = (new FileOutputStream(configFile));
                fileOutputStream.write(jsonObject.toJson(true, true).getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (Error | Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Read Config!");
        }));
        for (ModID mod : MOD_CONFIGS.keySet()) {
            builder.put(mod.toString(), screenBase -> new ScreenBuilder(screenBase, mod));
        }
    }

}
