package net.glasslauncher.mods.api.gcapi.impl.config.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.screen.IntegerListScreenBuilder;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.lang.reflect.*;
import java.util.*;

public class IntegerListConfigEntry extends BaseListConfigEntry<Integer> {

    public IntegerListConfigEntry(String id, String name, String description, Field parentField, Object parentObject, boolean multiplayerSynced, Integer[] value, MaxLength maxLength) {
        super(id, name, description, parentField, parentObject, multiplayerSynced, value, maxLength);
    }

    @Override
    public boolean isValueValid() {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<Integer> createListScreen() {
        net.glasslauncher.mods.api.gcapi.screen.BaseListScreenBuilder<Integer> listScreen = new IntegerListScreenBuilder(parent, maxLength, this, str -> BiTuple.of(CharacterUtils.isInteger(str) && Integer.parseInt(str) <= maxLength.value(), multiplayerLoaded? Collections.singletonList("Server synced, you cannot change this value") : CharacterUtils.isFloat(str)? Float.parseFloat(str) > maxLength.value()? Collections.singletonList("Value is too high") : null : Collections.singletonList("Value is not a whole number")));
        listScreen.setValues(value);
        return listScreen;
    }

    @Override
    public Integer strToVal(String str) {
        return Integer.parseInt(str);
    }
}
