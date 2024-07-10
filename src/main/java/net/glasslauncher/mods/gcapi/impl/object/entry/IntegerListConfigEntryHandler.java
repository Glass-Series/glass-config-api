package net.glasslauncher.mods.gcapi.impl.object.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi.api.ConfigEntry;
import net.glasslauncher.mods.gcapi.impl.screen.BaseListScreenBuilder;
import net.glasslauncher.mods.gcapi.impl.screen.IntegerListScreenBuilder;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.*;

public class IntegerListConfigEntryHandler extends BaseListConfigEntryHandler<Integer> {

    public IntegerListConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Integer[] value, Integer[] defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> IntegerConfigEntryHandler.integerValidator(configEntry, str);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BaseListScreenBuilder<Integer> createListScreen(Screen parent) {
        BaseListScreenBuilder<Integer> listScreen = new IntegerListScreenBuilder(parent,
                configEntry,
                this,
                textValidator
        );
        listScreen.setValues(value);
        return listScreen;
    }

    @Override
    public Integer strToVal(String str) {
        return Integer.parseInt(str);
    }

    @Override
    public Integer[] getTypedArray() {
        return new Integer[0];
    }
}
