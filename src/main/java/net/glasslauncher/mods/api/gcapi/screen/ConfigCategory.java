package net.glasslauncher.mods.api.gcapi.screen;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.minecraft.client.gui.screen.ScreenBase;
import org.jetbrains.annotations.NotNull;

public abstract class ConfigCategory extends ConfigBase {

    public ConfigCategory(String name, String description) {
        super(name, description);
    }

    /**
     * The ScreenBuilder for this category. Can only have config entries.
     * @return ScreenBuilder for either categories or confi
     */
    public @NotNull abstract ScreenBuilder getConfigScreen(ScreenBase parent, ModContainerEntrypoint mod);
}
