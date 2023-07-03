package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.MaxLength;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.util.*;
import java.util.function.*;

public class FloatListScreenBuilder extends BaseListScreenBuilder<Float> {

    public FloatListScreenBuilder(net.minecraft.client.gui.screen.ScreenBase parent, MaxLength maxLength, ConfigEntry<Float[]> configEntry, Function<String, BiTuple<Boolean, List<String>>> validator) {
        super(parent, maxLength, configEntry, validator);
    }

    @Override
    Float convertStringToValue(String value) {
        return Float.parseFloat(value);
    }
}
