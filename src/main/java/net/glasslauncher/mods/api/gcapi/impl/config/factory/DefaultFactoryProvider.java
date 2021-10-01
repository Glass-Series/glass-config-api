package net.glasslauncher.mods.api.gcapi.impl.config.factory;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.api.gcapi.api.ConfigFactoryProvider;
import net.glasslauncher.mods.api.gcapi.impl.config.ConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.BooleanConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.FloatConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.IntegerConfigEntry;
import net.glasslauncher.mods.api.gcapi.screen.widget.StringConfigEntry;
import uk.co.benjiweber.expressions.function.QuinFunction;

import java.lang.reflect.Type;

public class DefaultFactoryProvider implements ConfigFactoryProvider {

    @Override
    public void provideFactories(ImmutableMap.Builder<Type, QuinFunction<String, String, String, Object, Integer, ConfigEntry<?>>> immutableBuilder) {
        immutableBuilder.put(String.class, ((id, name, description, value, maxLength) -> new StringConfigEntry(id, name, description, value.toString(), maxLength)));
        immutableBuilder.put(Integer.class, ((id, name, description, value, maxLength) -> new IntegerConfigEntry(id, name, description, Integer.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Float.class, ((id, name, description, value, maxLength) -> new FloatConfigEntry(id, name, description, Float.valueOf(value.toString()), maxLength)));
        immutableBuilder.put(Boolean.class, ((id, name, description, value, maxLength) -> new BooleanConfigEntry(id, name, description, (boolean) value, maxLength)));
    }
}
