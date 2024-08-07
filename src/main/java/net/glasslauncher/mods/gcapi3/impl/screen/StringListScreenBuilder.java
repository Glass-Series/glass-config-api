package net.glasslauncher.mods.gcapi3.impl.screen;

import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.minecraft.client.gui.screen.Screen;

import java.util.*;
import java.util.function.*;

public class StringListScreenBuilder extends BaseListScreenBuilder<String> {

    public StringListScreenBuilder(Screen parent, ConfigEntry configAnnotation, ConfigEntryHandler<String[]> configEntry, Function<String, List<String>> validator) {
        super(parent, configAnnotation, configEntry, validator);
    }

    @Override
    public String convertStringToValue(String value) {
        return value;
    }
}
