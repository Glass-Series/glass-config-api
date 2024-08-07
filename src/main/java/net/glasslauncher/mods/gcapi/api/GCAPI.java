package net.glasslauncher.mods.gcapi.api;

import net.glasslauncher.mods.gcapi.impl.ConfigRootEntry;
import net.glasslauncher.mods.gcapi.impl.EventStorage;
import net.glasslauncher.mods.gcapi.impl.GCCore;
import net.glasslauncher.mods.gcapi.impl.GlassYamlFile;
import net.glasslauncher.mods.gcapi.impl.screen.RootScreenBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.*;
import java.io.*;

/**
 * Use this instead of GCCore!
 */
@SuppressWarnings("deprecation")
public class GCAPI {

    /**
     * Force a config reload, or load your own config json! Can be partial.
     * @param configID Should be an identifier formatted like mymodid:mygconfigvalue
     * @param overrideConfigJson Optional config override JSON. Leave as null to do a plain config reload. JSON can be partial, and missing values from the JSON will be kept.
     */
    public static void reloadConfig(Identifier configID, @Nullable String overrideConfigJson) throws IOException {
        reloadConfig(configID, new GlassYamlFile(overrideConfigJson));
    }

    /**
     * Force a config reload, or load your own config json! Can be partial.
     * @param configID Should be an identifier formatted like mymodid:mygconfigvalue
     * @param overrideConfigJson Optional config override JSON. Leave as null to do a plain config reload. JSON can be partial, and missing values from the JSON will be kept.
     */
    public static void reloadConfig(Identifier configID, @Nullable GlassYamlFile overrideConfigJson) {
        ConfigRootEntry category = GCCore.MOD_CONFIGS.get(configID);
        GCCore.loadModConfig(category.configRoot(), category.modContainer(), category.configCategoryHandler().parentField, configID, overrideConfigJson);
        GCCore.saveConfig(category.modContainer(), category.configCategoryHandler(), EventStorage.EventSource.MOD_SAVE);
    }

    /**
     * Force a config reload.
     * @param configID Should be an identifier formatted like mymodid:mygconfigvalue
     */
    public static void reloadConfig(Identifier configID) {
        reloadConfig(configID, (GlassYamlFile) null);
    }

    public static RootScreenBuilder getRootConfigScreen(@NotNull Identifier identifier, Screen parent) throws AttributeNotFoundException {
        ConfigRootEntry configRootEntry = GCCore.MOD_CONFIGS.get(identifier);
        if (configRootEntry != null) {
            return new RootScreenBuilder(parent, configRootEntry.modContainer(), configRootEntry.configCategoryHandler());
        }
        else {
            throw new AttributeNotFoundException(); // Probably the wrong error. Whatever.
        }
    }

}
