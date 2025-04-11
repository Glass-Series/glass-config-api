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

public class IntegerConfigEntryHandler extends ConfigEntryHandler<Integer> {
    private ExtensibleTextFieldWidget textbox;

    public IntegerConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Integer value, Integer defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> integerValidator(configEntry, str);
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
    public Integer getDrawableValue() {
        return textbox == null? null : Integer.parseInt(textbox.getText());
    }

    @Override
    public void setDrawableValue(Integer value) {
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
        value = (Integer) defaultValue;
        setDrawableValue((Integer) defaultValue);
        saveToField();
    }

    public static List<String> integerValidator(ConfigEntry configEntry, String str) {
        if (!CharacterUtils.isInteger(str)) {
            return Collections.singletonList("Value is not a whole number");
        }
        if (Integer.parseInt(str) > configEntry.maxLength()) {
            return Collections.singletonList("Value is too high");
        }
        if (Integer.parseInt(str) < configEntry.minLength()) {
            return Collections.singletonList("Value is too low");
        }
        return null;
    }
}
