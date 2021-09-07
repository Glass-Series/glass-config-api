package net.glasslauncher.mods.api.gcapi.screen.ownconfig;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.screen.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.ScreenBuilder;
import net.minecraft.client.gui.screen.ScreenBase;
import org.jetbrains.annotations.NotNull;

public class DefaultCategoryEntry extends ConfigCategory {

    public DefaultCategoryEntry(String name, String description) {
        super(name, description);
    }

    @Override
    public @NotNull ScreenBuilder getConfigScreen(ScreenBase parent, ModContainer mod) {
        return null;
    }
}
