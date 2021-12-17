package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.IntegerListScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IntegerListConfigEntry extends ConfigEntry<Integer[]> implements ConfigEntryWithButton {
    private IntegerListScreenBuilder listScreen;
    @Environment(EnvType.CLIENT)
    private FancyButton button;
    private final int maxLength;

    public IntegerListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean isMultiplayerSynced, Integer[] value, int maxLength) {
        super(id, name, description, parentField, parentObject, isMultiplayerSynced, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        button = new FancyButton(10, 0, 0, 0, 0, "Open List... (" + value.length + " values)");
        listScreen = new IntegerListScreenBuilder(parent, maxLength, this);
        listScreen.setValues(value);
        button.active = !multiplayerLoaded;
    }

    @Override
    public Integer[] getDrawableValue() {
        if (listScreen == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        listScreen.textboxes.forEach((val) -> list.add(Integer.parseInt(val.getText())));
        return list.toArray(new Integer[0]);
    }

    @Override
    public void setDrawableValue(Integer[] value) {
        listScreen.setValues(value);
    }

    @Override
    public boolean isValueValid() {
        return true;
    }

    @Override
    public @NotNull HasDrawable getDrawable() {
        return (HasDrawable) button;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick() {
        ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(listScreen);
    }
}

