package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButtonWidget;
import net.glasslauncher.mods.api.gcapi.screen.widget.IconWidget;
import net.glasslauncher.mods.api.gcapi.screen.widget.ResetConfigWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

public abstract class BaseListConfigEntry<T> extends ConfigEntry<T[]> implements ConfigEntryWithButton {
    @Environment(EnvType.CLIENT)
    private net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<T> listScreen;
    @Environment(EnvType.CLIENT)
    private FancyButtonWidget button;

    public BaseListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T[] value, T[] defaultValue, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, defaultValue, maxLength);
    }

    @Override
    public void init(Screen parent, TextRenderer textRenderer) {
        super.init(parent, textRenderer);
        button = new FancyButtonWidget(10, 0, 0, 0, 0, "Open List... (" + value.length + " values)");
        drawableList.add(button);
        listScreen = createListScreen();
        button.active = !multiplayerLoaded;
    }

    @Environment(EnvType.CLIENT)
    public abstract net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<T> createListScreen();

    public abstract T strToVal(String str);

    @Override
    public T[] getDrawableValue() {
        if (listScreen == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        listScreen.textFieldWidgets.forEach((val) -> {
            if (val.isValueValid()) {
                list.add(strToVal(val.getText()));
            }
        });
        //noinspection unchecked This class should only ever be used by arrays.
        return (T[]) list.toArray(new Object[0]);
    }

    @Override
    public void setDrawableValue(T[] value) {
        listScreen.setValues(value);
    }

    @Override
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick() {
        //noinspection deprecation
        ((net.minecraft.client.Minecraft) FabricLoader.getInstance().getGameInstance()).setScreen(listScreen);
    }

    @Override
    public void reset() throws IllegalAccessException { // !!OVERRIDE THIS AND DO A DEEP CLONE IF YOU'RE USING SOMETHING THAT ISN'T A PRIMITIVE!!
        if (!multiplayerLoaded) {
            parentField.set(parentObject, defaultValue);
            value = defaultValue.clone();
        }
    }
}

