package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.util.*;
import java.util.function.*;

public class IntegerListScreenBuilder extends BaseListScreenBuilder<Integer> {

    public IntegerListScreenBuilder(net.minecraft.client.gui.screen.ScreenBase parent, MaxLength maxLength, ConfigEntry<Integer[]> configEntry, Function<String, BiTuple<Boolean, List<String>>> validator) {
        super(parent, maxLength, configEntry, validator);
    }

    @Override
    Integer convertStringToValue(String value) {
        return Integer.parseInt(value);
    }
}
