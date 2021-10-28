package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.api.CharacterUtils;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;

import java.util.List;

public class FloatListScreenBuilder extends BaseListScreenBuilder<Float> {

    public FloatListScreenBuilder(ScreenBase parent, int maxLength, ConfigEntry<List<Float>> configEntry) {
        super(parent, maxLength, configEntry, CharacterUtils::isFloat);
    }

    @Override
    Float convertStringToValue(String value) {
        return Float.parseFloat(value);
    }
}
