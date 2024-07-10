package net.glasslauncher.mods.gcapi.impl.screen;

import net.glasslauncher.mods.gcapi.api.ConfigEntry;
import net.glasslauncher.mods.gcapi.impl.object.ConfigEntryHandler;
import net.minecraft.client.gui.screen.Screen;

import java.util.*;
import java.util.function.*;

public class IntegerListScreenBuilder extends BaseListScreenBuilder<Integer> {

    public IntegerListScreenBuilder(Screen parent, ConfigEntry maxLength, ConfigEntryHandler<Integer[]> configEntry, Function<String, List<String>> validator) {
        super(parent, maxLength, configEntry, validator);
    }

    @Override
    Integer convertStringToValue(String value) {
        return Integer.parseInt(value);
    }
}