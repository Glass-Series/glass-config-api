package net.glasslauncher.mods.api.gcapi.screen;

import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.minecraft.client.gui.screen.ScreenBase;

import java.util.List;

public class StringListScreenBuilder extends BaseListScreenBuilder<String> {

    public StringListScreenBuilder(ScreenBase parent, int maxLength, ConfigEntry<List<String>> configEntry) {
        super(parent, maxLength, configEntry, (val) -> true);
    }

    @Override
    String convertStringToValue(String value) {
        return value;
    }
}
