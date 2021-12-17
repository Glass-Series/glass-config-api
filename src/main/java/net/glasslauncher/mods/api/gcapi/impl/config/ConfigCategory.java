package net.glasslauncher.mods.api.gcapi.impl.config;

import com.google.common.collect.Multimap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.screen.RootScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.ScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButton;
import net.minecraft.client.gui.screen.ScreenBase;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class ConfigCategory extends ConfigBase {

    public final boolean isRoot;
    public Multimap<Class<?>, ConfigBase> values;

    private FancyButton button;

    public ConfigCategory(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, Multimap<Class<?>, ConfigBase> values, boolean isRoot) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.values = values;
        this.isRoot = isRoot;
    }

    /**
     * The ScreenBuilder for this category. Can only have config entries.
     * @return ScreenBuilder
     */
    @Environment(EnvType.CLIENT)
    public @NotNull ScreenBuilder getConfigScreen(ScreenBase parent, EntrypointContainer<Object> mod) {
        return isRoot ? new RootScreenBuilder(parent, mod, this) : new ScreenBuilder(parent, mod, this);
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        if (button == null) {
            button = new FancyButton(0, 0, 0, "Open");
        }
        return (HasDrawable) button;
    }
}
