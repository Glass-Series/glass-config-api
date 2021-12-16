package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextbox;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class StringConfigEntry extends ConfigEntry<String> {
    private ExtensibleTextbox textbox;
    private final int maxLength;

    public StringConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean isMultiplayerSynced, String value, int maxLength) {
        super(id, name, description, parentField, parentObject, isMultiplayerSynced, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        textbox = new ExtensibleTextbox(textRenderer, (text) -> true);
        textbox.setMaxLength(maxLength);
        textbox.setText(value);
        textbox.setEnabled(!multiplayerLoaded);
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
    public @NotNull HasDrawable getDrawable() {
        return textbox;
    }
}
