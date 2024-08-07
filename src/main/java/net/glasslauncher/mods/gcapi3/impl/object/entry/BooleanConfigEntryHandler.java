package net.glasslauncher.mods.gcapi3.impl.object.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigEntryWithButton;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.FancyButtonWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.*;
import java.util.List;

public class BooleanConfigEntryHandler extends ConfigEntryHandler<Boolean> implements ConfigEntryWithButton {
    private FancyButtonWidget button;

    public BooleanConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Boolean value, Boolean defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        button = new FancyButtonWidget(10, 0, 0, 0, 0, value.toString(), CharacterUtils.getIntFromColour(new Color(255, 202, 0, 255)));
        drawableList.add(button);
        button.active = !multiplayerLoaded;
    }

    @Override
    public Boolean getDrawableValue() {
        return value;
    }

    @Override
    public void setDrawableValue(Boolean value) {
        this.value = value;
        if(button != null) {
            button.text = value.toString();
        }
    }

    @Override
    public boolean isValueValid() {
        return true; // If this is *somehow* not a boolean, there's fuck all that can be done. You've already crashed before this check. :)
    }

    @Environment(EnvType.CLIENT)
    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick() {
        value = !value;
        button.text = value.toString();
    }

    @Override
    public void reset(Object defaultValue) throws IllegalAccessException {
        value = (Boolean) defaultValue;
        setDrawableValue((Boolean) defaultValue);
        saveToField();
    }
}
