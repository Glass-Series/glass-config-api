package net.glasslauncher.mods.api.gcapi.screen;

import com.google.common.collect.Multimap;
import net.glasslauncher.mods.api.gcapi.impl.ModContainerEntrypoint;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import org.jetbrains.annotations.NotNull;

public class ConfigCategory extends ConfigBase {

    public Multimap<Class<?>, ConfigBase> values;

    private final Button button;

    public ConfigCategory(String name, String description, Multimap<Class<?>, ConfigBase> values) {
        super(name, description);
        this.values = values;
        button = new Button(0, 0, 0, name);
    }

    /**
     * The ScreenBuilder for this category. Can only have config entries.
     * @return ScreenBuilder
     */
    public @NotNull ScreenBuilder getConfigScreen(ScreenBase parent, ModContainerEntrypoint mod) {
        return new ScreenBuilder(parent, mod, this);
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return (HasDrawable) button;
    }
}
