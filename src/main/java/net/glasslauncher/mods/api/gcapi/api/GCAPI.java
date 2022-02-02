package net.glasslauncher.mods.api.gcapi.api;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigCategory;
import net.modificationstation.stationapi.api.registry.Identifier;
import org.jetbrains.annotations.Nullable;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.util.concurrent.atomic.*;

/**
 * Use this instead of GCCore!
 */
public class GCAPI {

    /**
     * Force a config reload, or load your own config json! Can be partial.bbb
     * @param configID Should be an identifier formatted like
     * @param configJson Optional config overwrite
     */
    public static void reloadConfig(Identifier configID, @Nullable String configJson) {
        AtomicReference<Identifier> mod = new AtomicReference<>();
        GCCore.MOD_CONFIGS.keySet().forEach(modContainer -> {
            if (modContainer.toString().equals(configID.toString())) {
                mod.set(modContainer);
            }
        });
        if (mod.get() != null) {
            BiTuple<EntrypointContainer<Object>, ConfigCategory> category = GCCore.MOD_CONFIGS.get(mod.get());
            GCCore.loadModConfig(category.one().getEntrypoint(), category.one().getProvider(), category.two().parentField, mod.get(), configJson);
            GCCore.saveConfig(category.one(), category.two());
        }
    }

}
