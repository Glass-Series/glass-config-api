package net.glasslauncher.mods.gcapi3.impl.object.entry;

import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.ExtensibleTextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

public class FloatConfigEntryHandler extends ConfigEntryHandler<Float> {
    private ExtensibleTextFieldWidget textbox;

    public FloatConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Float value, Float defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> floatValidator(configEntry, str);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        textbox = new ExtensibleTextFieldWidget(textRenderer);
        textbox.setValidator(textValidator);
        textbox.setText(value.toString());
        textbox.setEnabled(!multiplayerLoaded);
        drawableList.add(textbox);
    }

    @Override
    public Float getDrawableValue() {
        return textbox == null? null : Float.parseFloat(textbox.getText());
    }

    @Override
    public void setDrawableValue(Float value) {
        textbox.setText(value.toString());
    }

    @Override
    public boolean isValueValid() {
        return textValidator.apply(value.toString()) == null;
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Override
    public void reset(Object defaultValue) throws IllegalAccessException {
        value = (Float) defaultValue;
        setDrawableValue((Float) defaultValue);
        saveToField();
    }

    public static List<String> floatValidator(ConfigEntry configEntry, String str) {
        if (!CharacterUtils.isFloat(str)) {
            return Collections.singletonList("Value is not a floating point number");
        }
        if (Float.parseFloat(str) > configEntry.maxLength()) {
            return Collections.singletonList("Value is too high");
        }
        if (Float.parseFloat(str) < configEntry.minLength()) {
            return Collections.singletonList("Value is too low");
        }
        return null;
    }
}
