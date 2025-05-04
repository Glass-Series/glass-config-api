package net.glasslauncher.mods.gcapi3.impl.object.entry;

import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
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

public class DoubleConfigEntryHandler extends ConfigEntryHandler<Double> {
    private ExtensibleTextFieldWidget textbox;

    public DoubleConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Double value, Double defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> doubleValidator(configEntry, str);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        textbox = new ExtensibleTextFieldWidget(textRenderer);
        textbox.setValidator(textValidator);
        textbox.setMaxLength(String.valueOf(Integer.MAX_VALUE).length());
        textbox.setText(value.toString());
        textbox.setEnabled(!multiplayerLoaded);
        textbox.setTextUpdatedListener(() -> {
            if (configEntry.requiresRestart() && parent instanceof ScreenBuilder screenBuilder) {
                screenBuilder.setRequiresRestart();
            }
        });
        drawableList.add(textbox);
    }

    @Override
    public Double getDrawableValue() {
        return textbox == null? null : Double.parseDouble(textbox.getText());
    }

    @Override
    public void setDrawableValue(Double value) {
        if (textbox == null) {
            return;
        }
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
        value = (Double) defaultValue;
        setDrawableValue((Double) defaultValue);
        saveToField();
    }

    public static List<String> doubleValidator(ConfigEntry configEntry, String str) {
        if (!CharacterUtils.isFloat(str)) { // Called isFloat, but it just looks for any valid decimal number.
            return Collections.singletonList("Value is not a whole number");
        }
        if (Double.parseDouble(str) > Math.round(configEntry.maxValue() == 32d ? configEntry.maxLength() : configEntry.maxValue())) {
            return Collections.singletonList("Value is too high");
        }
        if (Double.parseDouble(str) < (configEntry.minValue() == 0d ? configEntry.minLength() : configEntry.minValue())) {
            return Collections.singletonList("Value is too low");
        }
        return null;
    }
}
