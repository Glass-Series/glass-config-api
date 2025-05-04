package net.glasslauncher.mods.gcapi3.impl.object.entry;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.screen.ScreenBuilder;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.ExtensibleTextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class StringConfigEntryHandler extends ConfigEntryHandler<String> {
    private ExtensibleTextFieldWidget textbox;

    public StringConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, String value, String defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> stringValidator(configEntry, str);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        textbox = new ExtensibleTextFieldWidget(textRenderer);
        textbox.setValidator(textValidator);
        textbox.setMaxLength(Math.toIntExact(configEntry.maxValue() == 32d ? configEntry.maxLength() : configEntry.maxLength()));
        textbox.setText(value);
        textbox.setEnabled(!multiplayerLoaded);
        textbox.setTextUpdatedListener(() -> {
            if (configEntry.requiresRestart() && parent instanceof ScreenBuilder screenBuilder) {
                screenBuilder.setRequiresRestart();
            }
        });
        drawableList.add(textbox);
    }

    @Override
    public String getDrawableValue() {
        return textbox == null? null : textbox.getText();
    }

    @Override
    public void setDrawableValue(String value) {
        if (textbox == null) {
            return;
        }
        textbox.setText(value);
    }

    @Override
    public boolean isValueValid() {
        return true;
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Override
    public void reset(Object defaultValue) throws IllegalAccessException {
        value = (String) defaultValue;
        setDrawableValue((String) defaultValue);
        saveToField();
    }

    public static List<String> stringValidator(ConfigEntry configEntry, String str) {
        if (str.length() > Math.round(configEntry.maxValue() == 32d ? configEntry.maxLength() : configEntry.maxValue())) {
            return Collections.singletonList("Value is too long");
        }
        if (str.length() < (configEntry.minValue() == 32d ? configEntry.minLength() : configEntry.minValue())) {
            return Collections.singletonList("Value is too short");
        }
        return null;
    }
}
