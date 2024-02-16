package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButtonWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.*;
import java.util.List;

public class BooleanConfigEntry extends ConfigEntry<Boolean> implements ConfigEntryWithButton {
    private FancyButtonWidget button;

    public BooleanConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, Boolean value, Boolean defaultValue) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, defaultValue, null);
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
        button.text = value.toString();
    }

    @Override
    public boolean isValueValid() {
        return true;
    }

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
