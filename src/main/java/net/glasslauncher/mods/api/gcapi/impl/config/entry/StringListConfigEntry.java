package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.screen.StringListScreenBuilder;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.lang.reflect.*;

public class StringListConfigEntry extends BaseListConfigEntry<String> {

    public StringListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, String[] value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, maxLength);
    }

    @Override
    public boolean isValueValid() {
        return value.length < maxLength.arrayValue();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<String> createListScreen() {
        net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<String> listScreen = new StringListScreenBuilder(parent, maxLength, this, str -> BiTuple.of(true, null));
        listScreen.setValues(value);
        return listScreen;
    }

    @Override
    public String strToVal(String str) {
        return null;
    }
}

