package net.glasslauncher.mods.api.gcapi.impl.config;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.glasslauncher.mods.api.gcapi.screen.ScreenBuilder;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import org.jetbrains.annotations.NotNull;

public class ConfigCategory extends ConfigBase {

    public Multimap<Class<?>, ConfigBase> values;

    private Button button;

    public ConfigCategory(String id, String name, String description, Multimap<Class<?>, ConfigBase> values) {
        super(id, name, description);
        this.values = values;
    }

    /**
     * The ScreenBuilder for this category. Can only have config entries.
     * @return ScreenBuilder
     */
    @Environment(EnvType.CLIENT)
    public @NotNull ScreenBuilder getConfigScreen(ScreenBase parent, ModContainerEntrypoint mod) {
        return new ScreenBuilder(parent, mod, this);
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        if (button == null) {
            button = new Button(0, 0, 0, "Open");
        }
        return (HasDrawable) button;
    }
}
