package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;

import java.util.List;

public class IntegerListScreenBuilder extends BaseListScreenBuilder<Integer> {

    public IntegerListScreenBuilder(ScreenBase parent, int maxLength, ConfigEntry<List<Integer>> configEntry) {
        super(parent, maxLength, configEntry, CharacterUtils::isInteger);
    }

    @Override
    Integer convertStringToValue(String value) {
        return Integer.parseInt(value);
    }
}
