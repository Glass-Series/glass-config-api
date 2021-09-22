package net.glasslauncher.mods.api.gcapi.screen.ownconfig;

import net.fabricmc.loader.api.ModContainer;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.glasslauncher.mods.api.gcapi.screen.ConfigCategory;
import net.glasslauncher.mods.api.gcapi.screen.HasDrawable;
import net.glasslauncher.mods.api.gcapi.screen.ScreenBuilder;
import net.minecraft.client.gui.screen.ScreenBase;
import org.jetbrains.annotations.NotNull;

public class DefaultConfigCategory extends ConfigCategory {

    public DefaultConfigCategory(String name, String description) {
        super(name, description);
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return null;
    }

    @Override
    public @NotNull ScreenBuilder getConfigScreen(ScreenBase parent, ModContainerEntrypoint mod) {
        return new ScreenBuilder(parent, mod);
    }
}
