package net.glasslauncher.mods.gcapi3.impl.object.entry;

import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.api.CharacterUtils;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigEntryWithButton;
import net.glasslauncher.mods.gcapi3.api.HasDrawable;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.screen.widget.FancyButtonWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.TranslationStorage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.reflect.*;
import java.util.List;
import java.util.*;

/**
 * This class is a bit of a crapshoot cause java's generic type handling is pitifully bad.
 * @param <T> The enum you want to use. Must have toString implemented. Also must be passed into the constructor.
 */
public class EnumConfigEntryHandler<T extends Enum<?>> extends ConfigEntryHandler<Integer> implements ConfigEntryWithButton {
    private FancyButtonWidget button;
    public final Enum<?>[] parentEnumArray;

    public EnumConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Integer value, Integer defaultValue, @SuppressWarnings("rawtypes") Class parentEnum) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        //noinspection unchecked Fuck off
        parentEnumArray = (Enum<?>[]) Iterables.toArray(EnumSet.allOf(parentEnum), parentEnum);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        button = new FancyButtonWidget(10, 0, 0, 0, 0, getButtonText(), CharacterUtils.getIntFromColour(new Color(255, 202, 0, 255)));
        drawableList.add(button);
        button.active = !multiplayerLoaded;
    }

    @Override
    public Integer getDrawableValue() {
        return value;
    }

    @Override
    public void setDrawableValue(Integer value) {
        this.value = value;
        if(button != null) {
            button.text = getButtonText();
        }
    }

    @Override
    public boolean isValueValid() {
        return value < parentEnumArray.length;
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick() {
        value++;
        if(value > parentEnumArray.length - 1) {
            value = 0;
        }
        button.text = getButtonText();
    }

    @Override
    public void reset(Object defaultValue) throws IllegalAccessException {
        value = (Integer) defaultValue;
        setDrawableValue((Integer) defaultValue);
        saveToField();
    }

    @Override
    public void saveToField() throws IllegalAccessException {
        parentField.set(parentObject, parentEnumArray[value]);
    }

    public String getButtonText() {
        return TranslationStorage.getInstance().get(parentEnumArray[value].toString()) + " (" + (value + 1) + "/" + parentEnumArray.length + ")";
    }
}
