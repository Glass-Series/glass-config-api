package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.ExtensibleTextFieldWidget;
import net.glasslauncher.mods.api.gcapi.screen.widget.IconWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.lang.reflect.*;
import java.util.*;

public class IntegerConfigEntry extends ConfigEntry<Integer> {
    private ExtensibleTextFieldWidget textbox;
    private List<HasDrawable> drawableList;

    public IntegerConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, Integer value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, maxLength);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        textbox = new ExtensibleTextFieldWidget(textRenderer);
        textbox.setValidator(str -> BiTuple.of(CharacterUtils.isInteger(str) && Integer.parseInt(str) <= maxLength.value(), multiplayerLoaded? Collections.singletonList("Server synced, you cannot change this value") : CharacterUtils.isFloat(str)? Float.parseFloat(str) > maxLength.value()? Collections.singletonList("Value is too high") : null : Collections.singletonList("Value is not a whole number")));
        textbox.setMaxLength(maxLength.value());
        textbox.setText(value.toString());
        textbox.setEnabled(!multiplayerLoaded);
        drawableList = new ArrayList<>() {{
            add(textbox);
        }};
        if (multiplayerSynced) {
            drawableList.add(new IconWidget(10, 0, 0, 0, "/assets/gcapi/server_synced.png"));
        }
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
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }
}
