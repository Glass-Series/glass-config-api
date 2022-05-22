package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextbox;
import net.glasslauncher.mods.api.gcapi.screen.widget.Icon;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

public class StringConfigEntry extends ConfigEntry<String> {
    private ExtensibleTextbox textbox;
    private List<HasDrawable> drawableList;

    public StringConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean isMultiplayerSynced, String value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, isMultiplayerSynced, value, maxLength);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        textbox = new ExtensibleTextbox(textRenderer);
        textbox.setMaxLength(maxLength.value());
        textbox.setText(value);
        textbox.setEnabled(!multiplayerLoaded);
        drawableList = new ArrayList<>() {{
            add(textbox);
        }};
        if (multiplayerSynced) {
            drawableList.add(new Icon(10, 0, 0, 0, "/assets/gcapi/server_synced.png"));
        }
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
}
