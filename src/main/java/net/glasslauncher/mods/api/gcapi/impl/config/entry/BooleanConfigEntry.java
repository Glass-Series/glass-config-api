package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

public class BooleanConfigEntry extends ConfigEntry<Boolean> implements ConfigEntryWithButton {
    private Button button;

    public BooleanConfigEntry(String id, String name, String description, Boolean value, int maxLength) {
        super(id, name, description, value);
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        button = new Button(10, 0, 0, 0, 0, value.toString());
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
    public @NotNull HasDrawable getDrawable() {
        return (HasDrawable) button;
    }

    @Override
    public void onClick() {
        value = !value;
        button.text = value.toString();
    }
}
