package net.glasslauncher.mods.api.gcapi.impl.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;

import java.lang.reflect.Field;

public abstract class ConfigEntry<T> extends ConfigBase {
    public T value;
    @Environment(EnvType.CLIENT)
    protected ScreenBase parent;
    @Environment(EnvType.CLIENT)
    protected TextRenderer textRenderer;
    public boolean multiplayerLoaded = false;

    public ConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T value) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.value = value;
    }

    @Environment(EnvType.CLIENT)
    public abstract void init(ScreenBase parent, TextRenderer textRenderer);

    public abstract T getDrawableValue();
    public abstract void setDrawableValue(T value);

    public abstract boolean isValueValid();

    public void saveToField() throws IllegalAccessException {
        parentField.set(parentObject, value);
    }
}
