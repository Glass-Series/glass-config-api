package net.glasslauncher.mods.gcapi3.impl.object;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.DefaultOnVanillaServer;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.api.TriBoolean;
import net.glasslauncher.mods.gcapi3.api.ValueOnVanillaServer;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.IconWidget;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.ResetConfigWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public abstract class ConfigEntryHandler<T> extends ConfigHandlerBase {
    public T value;
    public final T defaultValue;
    public boolean multiplayerLoaded = false;
    protected ConfigEntry configEntry;
    protected List<HasDrawable> drawableList = new ArrayList<>(){};
    /**
     * Optional convenience field for when using text boxes.
     * {@link net.glasslauncher.mods.gcapi3.impl.screen.widget.ExtensibleTextFieldWidget}
     */
    protected Function<String, List<String>> textValidator;

    public ConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, T value, T defaultValue) {
        super(id, configEntry.name(), configEntry.nameKey(), configEntry.description(), configEntry.descriptionKey(), parentField, parentObject, multiplayerSynced);
        this.configEntry = configEntry;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Environment(EnvType.CLIENT)
    public void init(Screen parent, TextRenderer textRenderer) {
        drawableList = new ArrayList<>();
        if (multiplayerSynced) {
            drawableList.add(new IconWidget(10, -5, 0, 0, "/assets/gcapi3/server_synced.png"));
        }
        drawableList.add(new ResetConfigWidget(10, -5, 0, 0, this));
    }

    public abstract T getDrawableValue();
    public abstract void setDrawableValue(T value);

    public abstract boolean isValueValid();

    public void saveToField() throws IllegalAccessException {
        parentField.set(parentObject, value);
    }

    /**
     * Called when resetting the entry to the default value.
     * Yes, I'm making you write this part yourself, I don't know how your custom objects work and how to properly deep clone them.
     * @throws IllegalAccessException Reflection can be used inside here without try/catch.
     */
    abstract public void reset(Object defaultValue) throws IllegalAccessException;

    /**
     * This is called on all ConfigEntry objects when joining a vanilla server.
     * Things done in here probably shouldn't be saved, so make sure you set multiplayerLoaded to true if you do anything.
     */
    public void vanillaServerBehavior() {
        try {
            if(parentField.getAnnotation(DefaultOnVanillaServer.class) != null) {
                multiplayerLoaded = true;
                reset(defaultValue);
            }
            else if (parentField.getAnnotation(ValueOnVanillaServer.class) != null) {
                ValueOnVanillaServer valueOnVanillaServer = parentField.getAnnotation(ValueOnVanillaServer.class);
                multiplayerLoaded = true;
                if (!valueOnVanillaServer.stringValue().isEmpty()) {
                    reset(valueOnVanillaServer.stringValue());
                }
                else if (valueOnVanillaServer.booleanValue() != TriBoolean.DEFAULT) {
                    reset(valueOnVanillaServer.booleanValue().value);
                }
                else if (valueOnVanillaServer.integerValue() != 0) {
                    reset(valueOnVanillaServer.integerValue());
                }
                else if (valueOnVanillaServer.floatValue() != 0) {
                    reset(valueOnVanillaServer.floatValue());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
