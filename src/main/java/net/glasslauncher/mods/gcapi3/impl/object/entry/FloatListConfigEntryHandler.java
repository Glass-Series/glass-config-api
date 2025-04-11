package net.glasslauncher.mods.gcapi3.impl.object.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.screen.BaseListScreenBuilder;
import net.glasslauncher.mods.gcapi3.impl.screen.FloatListScreenBuilder;
import net.minecraft.client.gui.screen.Screen;

import java.lang.reflect.Field;
import java.util.Arrays;

public class FloatListConfigEntryHandler extends BaseListConfigEntryHandler<Float> {

    public FloatListConfigEntryHandler(String id, ConfigEntry configEntry, Field parentField, Object parentObject, boolean multiplayerSynced, Float[] value, Float[] defaultValue) {
        super(id, configEntry, parentField, parentObject, multiplayerSynced, value, defaultValue);
        textValidator = str -> FloatConfigEntryHandler.floatValidator(configEntry, str);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BaseListScreenBuilder<Float> createListScreen(Screen parent) {
        BaseListScreenBuilder<Float> listScreen = new FloatListScreenBuilder(parent,
                configEntry,
                this,
                textValidator,
                textUpdatedListener
        );

        listScreen.setValues(value);
        return listScreen;
    }

    @Override
    public Float strToVal(String str) {
        return Float.parseFloat(str);
    }

    @Override
    public Float[] getTypedArray() {
        return new Float[0];
    }

    @Override
    public boolean listContentsValid() {
        return Arrays.stream(value).noneMatch(aFloat -> textValidator.apply(aFloat.toString()) != null);
    }
}

