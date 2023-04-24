package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.ConfigEntryWithButton;
import net.glasslauncher.mods.api.gcapi.api.HasDrawable;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder;
import net.glasslauncher.mods.api.gcapi.screen.widget.FancyButton;
import net.glasslauncher.mods.api.gcapi.screen.widget.Icon;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;

public abstract class BaseListConfigEntry<T> extends ConfigEntry<T[]> implements ConfigEntryWithButton {
    private BaseListScreenBuilder<T> listScreen;
    @Environment(EnvType.CLIENT)
    private FancyButton button;
    private List<HasDrawable> drawableList;

    public BaseListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, T[] value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, maxLength);
    }

    @Override
    public void init(net.minecraft.client.gui.screen.ScreenBase parent, net.minecraft.client.render.TextRenderer textRenderer) {
        button = new FancyButton(10, 0, 0, 0, 0, "Open List... (" + value.length + " values)");
        drawableList = new ArrayList<>() {{
            add(button);
        }};
        if (multiplayerSynced) {
            drawableList.add(new Icon(10, 0, 0, 0, "/assets/gcapi/server_synced.png"));
        }
        listScreen = createListScreen();
        button.active = !multiplayerLoaded;
    }

    public abstract BaseListScreenBuilder<T> createListScreen();

    public abstract T strToVal(String str);

    @Override
    public T[] getDrawableValue() {
        if (listScreen == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        listScreen.textboxes.forEach((val) -> {
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
        ((net.minecraft.client.Minecraft) FabricLoader.getInstance().getGameInstance()).openScreen(listScreen);
    }
}

