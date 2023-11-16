package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.Screen;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.util.*;
import java.util.function.*;

public class StringListScreenBuilder extends BaseListScreenBuilder<String> {

    public StringListScreenBuilder(Screen parent, MaxLength maxLength, ConfigEntry<String[]> configEntry, Function<String, BiTuple<Boolean, List<String>>> validator) {
        super(parent, maxLength, configEntry, validator);
    }

    @Override
    String convertStringToValue(String value) {
        return value;
    }
}
