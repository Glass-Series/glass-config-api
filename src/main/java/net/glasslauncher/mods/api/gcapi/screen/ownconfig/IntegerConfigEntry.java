package net.glasslauncher.mods.api.gcapi.screen.ownconfig;

import net.glasslauncher.mods.api.gcapi.impl.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.impl.ExtensibleTextbox;
import net.glasslauncher.mods.api.gcapi.screen.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.HasDrawable;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

public class IntegerConfigEntry extends ConfigEntry<Integer> {
    private ExtensibleTextbox textbox;
    private int maxLength;

    public IntegerConfigEntry(String id, String name, String description, Integer value, int maxLength) {
        super(id, name, description, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        textbox = new ExtensibleTextbox(textRenderer, CharacterUtils::isInteger);
        textbox.setMaxLength(maxLength);
        textbox.setText(value.toString());
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
        return textbox.isValueValid();
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return textbox;
    }
}
