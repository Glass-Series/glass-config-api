package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextFieldWidget;
import net.glasslauncher.mods.api.gcapi.screen.widget.IconWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

public class StringConfigEntry extends ConfigEntry<String> {
    private ExtensibleTextFieldWidget textbox;

    public StringConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, String value, String defaultValue, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, defaultValue, maxLength);
        this.maxLength = maxLength;
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        textbox = new ExtensibleTextFieldWidget(textRenderer);
        textbox.setMaxLength(maxLength.value());
        textbox.setText(value);
        textbox.setEnabled(!multiplayerLoaded);
        drawableList.add(textbox);
    }

    @Override
    public String getDrawableValue() {
        return textbox == null? null : textbox.getText();
    }

    @Override
    public void setDrawableValue(String value) {
        textbox.setText(value);
    }

    @Override
    public boolean isValueValid() {
        return textbox.isValueValid();
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Override
    public void reset(Object defaultValue) throws IllegalAccessException {
        if (!multiplayerLoaded) {
            parentField.set(parentObject, defaultValue);
            value = (String) defaultValue;
            setDrawableValue((String) defaultValue);
        }
    }
}
