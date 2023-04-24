package net.glasslauncher.mods.api.gcapi.impl.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;

import java.lang.reflect.*;

public abstract class ConfigEntry<T> extends ConfigBase {
    public T value;
    @Environment(EnvType.CLIENT)
    protected net.minecraft.client.gui.screen.ScreenBase parent;
    @Environment(EnvType.CLIENT)
    protected net.minecraft.client.render.TextRenderer textRenderer;
    public boolean multiplayerLoaded = false;
    protected MaxLength maxLength;

    public ConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.maxLength = maxLength;
        this.value = value;
    }

    @Environment(EnvType.CLIENT)
    public abstract void init(net.minecraft.client.gui.screen.ScreenBase parent, net.minecraft.client.render.TextRenderer textRenderer);

    public abstract T getDrawableValue();
    public abstract void setDrawableValue(T value);

    public abstract boolean isValueValid();

    public void saveToField() throws IllegalAccessException {
        if (!multiplayerLoaded) {
            parentField.set(parentObject, value);
        }
    }

    public MaxLength getMaxLength() {
        return maxLength;
    }
}
