package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.FloatListScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButton;
import net.glasslauncher.mods.api.gcapi.screen.widget.Icon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ScreenBase;
import net.minecraft.client.render.TextRenderer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FloatListConfigEntry extends ConfigEntry<Float[]> implements ConfigEntryWithButton {
    private FloatListScreenBuilder listScreen;
    @Environment(EnvType.CLIENT)
    private FancyButton button;
    private final int maxLength;
    private List<HasDrawable> drawableList;

    public FloatListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean isMultiplayerSynced, Float[] value, int maxLength) {
        super(id, name, description, parentField, parentObject, isMultiplayerSynced, value);
        this.maxLength = maxLength;
    }

    @Override
    public void init(ScreenBase parent, TextRenderer textRenderer) {
        button = new FancyButton(10, 0, 0, 0, 0, "Open List... (" + value.length + " values)");
        drawableList = new ArrayList<>() {{
            add(button);
        }};
        if (multiplayerSynced) {
            drawableList.add(new Icon(10, 0, 0, 0, "/assets/gcapi/server_synced.png"));
        }
        listScreen = new FloatListScreenBuilder(parent, maxLength, this);
        listScreen.setValues(value);
        button.active = !multiplayerLoaded;
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
        return list.toArray(new Float[0]);
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
    public @NotNull List<HasDrawable> getDrawables() {
        return drawableList;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClick() {
        //noinspection deprecation
        ((Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(listScreen);
    }
}

