package net.glasslauncher.mods.gcapi.api;

import net.glasslauncher.mods.gcapi.impl.ConfigRootEntry;
import net.glasslauncher.mods.gcapi.impl.EventStorage;
import net.glasslauncher.mods.gcapi.impl.GCCore;
import net.glasslauncher.mods.gcapi.impl.GlassYamlFile;
import net.modificationstation.stationapi.api.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.concurrent.atomic.*;

/**
 * Use this instead of GCCore!
 */
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
    @SuppressWarnings("deprecation")
    public static void reloadConfig(Identifier configID, @Nullable GlassYamlFile overrideConfigJson) {
        AtomicReference<Identifier> mod = new AtomicReference<>();
        GCCore.MOD_CONFIGS.keySet().forEach(modContainer -> {
            if (modContainer.equals(configID)) {
                ConfigRootEntry category = GCCore.MOD_CONFIGS.get(mod.get());
                GCCore.loadModConfig(category.configRoot(), category.modContainer(), category.configCategoryHandler().parentField, mod.get(), overrideConfigJson);
                GCCore.saveConfig(category.modContainer(), category.configCategoryHandler(), EventStorage.EventSource.MOD_SAVE);
            }
        });
    }

    /**
     * Force a config reload.
     * @param configID Should be an identifier formatted like mymodid:mygconfigvalue
     */
    public static void reloadConfig(Identifier configID) {
        reloadConfig(configID, (GlassYamlFile) null);
    }

}
