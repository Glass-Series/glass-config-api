package net.glasslauncher.mods.gcapi3.impl.object;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.api.*;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.ResetConfigWidget;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.RestartRequiredWidget;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.ServerSyncedWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ConfigEntryHandler<T> extends ConfigHandlerBase {
    public T value;
    public final T defaultValue;
    protected T valueBeforeVanilla;
    protected boolean vanillaLoaded = false;
    public boolean multiplayerLoaded = false;
    protected ConfigEntry configEntry;
    protected List<HasDrawable> drawableList = new ArrayList<>(){};
    /**
     * Optional convenience field for when using text boxes.
     * {@link net.glasslauncher.mods.gcapi3.impl.screen.widget.ExtensibleTextFieldWidget}
     */
    protected Function<String, List<String>> textValidator;
    protected Screen parent;

    public ConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, T value, T defaultValue) {
        super(id, configEntry.name(), configEntry.nameKey(), configEntry.description(), configEntry.descriptionKey(), parentField, parentObject, multiplayerSynced);
        this.configEntry = configEntry;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    @Environment(EnvType.CLIENT)
    public void init(Screen parent, TextRenderer textRenderer) {
        this.parent = parent;
        drawableList = new ArrayList<>();
        if (multiplayerSynced) {
            drawableList.add(new ServerSyncedWidget());
        }
        if (configEntry.requiresRestart()) {
            drawableList.add(new RestartRequiredWidget());
        }
        drawableList.add(new ResetConfigWidget(this));
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
        valueBeforeVanilla = value;
        try {
            if(parentField.getAnnotation(DefaultOnVanillaServer.class) != null) {
                multiplayerLoaded = true;
                vanillaLoaded = true;
                reset(defaultValue);
            }
            else if (parentField.getAnnotation(ValueOnVanillaServer.class) != null) {
                ValueOnVanillaServer valueOnVanillaServer = parentField.getAnnotation(ValueOnVanillaServer.class);
                multiplayerLoaded = true;
                vanillaLoaded = true;
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

    public void undoVanillaServerBehaviour() {
        if (!vanillaLoaded) {
            return;
        }
        try {
            reset(valueBeforeVanilla);
            multiplayerLoaded = false;
            vanillaLoaded = false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
