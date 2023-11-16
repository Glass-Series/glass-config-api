package net.glasslauncher.mods.api.gcapi.impl.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.*;

public abstract class ConfigEntry<T> extends ConfigBase {
    public T value;
    @Environment(EnvType.CLIENT)
    protected Screen parent;
    public boolean multiplayerLoaded = false;
    protected MaxLength maxLength;

    public ConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.maxLength = maxLength;
        this.value = value;
    }

    @Environment(EnvType.CLIENT)
    public abstract void init(Screen parent, TextRenderer textRenderer);

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
