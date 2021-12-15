package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.FloatListScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FloatListConfigEntry extends ConfigEntry<Float[]> implements ConfigEntryWithButton {
    private FloatListScreenBuilder listScreen;
    @Environment(EnvType.CLIENT)
    private Button button;
    private final int maxLength;

    public FloatListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean isMultiplayerSynced, Float[] value, int maxLength) {
        super(id, name, description, parentField, parentObject, isMultiplayerSynced, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        button = new Button(10, 0, 0, 0, 0, "Open List... (" + value.length + " values)");
        listScreen = new FloatListScreenBuilder(parent, maxLength, this);
        listScreen.setValues(value);
    }

    @Override
    public Float[] getDrawableValue() {
        if (listScreen == null) {
            return null;
        }
        List<Float> list = new ArrayList<>();
        listScreen.textboxes.forEach((val) -> {
            if (val.isValueValid()) {
                list.add(Float.parseFloat(val.getText()));
            }
        });
        return (Float[]) list.toArray();
    }

    @Override
    public void setDrawableValue(Float[] value) {
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

