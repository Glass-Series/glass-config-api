package net.glasslauncher.mods.gcapi3.impl.screen;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Function;

public class StringListScreenBuilder extends BaseListScreenBuilder<String> {

    /**
     * Slated for removal in 4.0.
     */
    @Deprecated
    public StringListScreenBuilder(Screen parent, ConfigEntry configAnnotation, ConfigEntryHandler<String[]> configEntry, Function<String, List<String>> validator) {
        super(parent, configAnnotation, configEntry, validator);
    }

    public StringListScreenBuilder(Screen parent, ConfigEntry configAnnotation, ConfigEntryHandler<String[]> configEntry, Function<String, List<String>> validator, Runnable textUpdatedListener) {
        super(parent, configAnnotation, configEntry, validator, textUpdatedListener);
    }

    @Override
    public String convertStringToValue(String value) {
        return value;
    }
}
