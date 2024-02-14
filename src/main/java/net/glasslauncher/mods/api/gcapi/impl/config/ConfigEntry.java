package net.glasslauncher.mods.api.gcapi.impl.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.DefaultOnVanillaServer;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.api.ValueOnVanillaServer;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.glasslauncher.mods.api.gcapi.screen.widget.IconWidget;
import net.glasslauncher.mods.api.gcapi.screen.widget.ResetConfigWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.*;
import java.util.*;

public abstract class ConfigEntry<T> extends ConfigBase {
    public T value;
    public final T defaultValue;
    @Environment(EnvType.CLIENT)
    protected Screen parent;
    public boolean multiplayerLoaded = false;
    protected MaxLength maxLength;
    protected List<HasDrawable> drawableList = new ArrayList<>(){};;

    public ConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T value, T defaultValue, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced);
        this.maxLength = maxLength;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Environment(EnvType.CLIENT)
    public void init(Screen parent, TextRenderer textRenderer) {
        drawableList = new ArrayList<>();
        if (multiplayerSynced) {
            drawableList.add(new IconWidget(10, -5, 0, 0, "/assets/gcapi/server_synced.png"));
        }
        drawableList.add(new ResetConfigWidget(10, -5, 0, 0, this));
    }

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

    /**
     * Called when resetting the entry to the default value.
     * Yes, I'm making you write this part yourself, I don't know how your custom objects work and how to properly deep clone them.
     * @throws IllegalAccessException Reflection can be used inside here without try/catch.
     */
    abstract public void reset(Object defaultValue) throws IllegalAccessException;

    public void vanillaServerBehavior() {
        try {
            if(parentField.getAnnotation(DefaultOnVanillaServer.class) != null) {
                multiplayerLoaded = true;
                    reset(defaultValue);
            }
            else if (parentField.getAnnotation(ValueOnVanillaServer.class) != null) {
                ValueOnVanillaServer valueOnVanillaServer = parentField.getAnnotation(ValueOnVanillaServer.class);
                multiplayerLoaded = true;
                if (valueOnVanillaServer.stringValue() != null) {
                    reset(valueOnVanillaServer.stringValue());
                }
                if (valueOnVanillaServer.booleanValue()) {
                    reset(true);
                }
                if (valueOnVanillaServer.integerValue() != 0) {
                    reset(valueOnVanillaServer.integerValue());
                }
                if (valueOnVanillaServer.floatValue() != 0) {
                    reset(valueOnVanillaServer.floatValue());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
