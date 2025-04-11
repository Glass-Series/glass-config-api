package net.glasslauncher.mods.gcapi3.impl.screen;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.function.Function;

public class FloatListScreenBuilder extends BaseListScreenBuilder<Float> {

    /**
     * Slated for removal in 4.0.
     */
    @Deprecated
    public FloatListScreenBuilder(Screen parent, ConfigEntry configAnnotation, ConfigEntryHandler<Float[]> configEntry, Function<String, List<String>> validator) {
        super(parent, configAnnotation, configEntry, validator);
    }

    public FloatListScreenBuilder(Screen parent, ConfigEntry configAnnotation, ConfigEntryHandler<Float[]> configEntry, Function<String, List<String>> validator, Runnable textUpdatedListener) {
        super(parent, configAnnotation, configEntry, validator, textUpdatedListener);
    }

    @Override
    Float convertStringToValue(String value) {
        return Float.parseFloat(value);
    }
}
