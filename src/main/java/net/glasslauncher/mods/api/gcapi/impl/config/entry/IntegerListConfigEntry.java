package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.IntegerListScreenBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.gui.widgets.Button;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IntegerListConfigEntry extends ConfigEntry<List<Integer>> implements ConfigEntryWithButton {
    private IntegerListScreenBuilder listScreen;
    private Button button;
    private final int maxLength;

    public IntegerListConfigEntry(String id, String name, String description, List<Integer> value, int maxLength) {
        super(id, name, description, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        button = new Button(10, 0, 0, 0, 0, "Open List...");
        listScreen = new IntegerListScreenBuilder(parent, maxLength, this);
        listScreen.setValues(value);
    }

    @Override
    public List<Integer> getDrawableValue() {
        if (listScreen == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        listScreen.textboxes.forEach((val) -> list.add(Integer.parseInt(val.getText())));
        return list;
    }

    @Override
    public void setDrawableValue(List<Integer> value) {
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

    @Override
    public void onClick() {
        ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(listScreen);
    }
}

