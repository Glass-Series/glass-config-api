package net.glasslauncher.mods.gcapi3.impl.screen;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Function;

public class IntegerListScreenBuilder extends BaseListScreenBuilder<Integer> {

    /**
     * Slated for removal in 4.0.
     */
    @Deprecated
    public IntegerListScreenBuilder(Screen parent, ConfigEntry maxLength, ConfigEntryHandler<Integer[]> configEntry, Function<String, List<String>> validator) {
        super(parent, maxLength, configEntry, validator);
    }

    public IntegerListScreenBuilder(Screen parent, ConfigEntry maxLength, ConfigEntryHandler<Integer[]> configEntry, Function<String, List<String>> validator, Runnable textUpdatedListener) {
        super(parent, maxLength, configEntry, validator, textUpdatedListener);
    }

    @Override
    Integer convertStringToValue(String value) {
        return Integer.parseInt(value);
    }
}
