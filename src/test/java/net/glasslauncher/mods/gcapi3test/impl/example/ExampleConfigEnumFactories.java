package net.glasslauncher.mods.gcapi3test.impl.example;

import com.google.common.collect.ImmutableMap;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;
import net.glasslauncher.mods.gcapi3.api.ConfigFactoryProvider;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.entry.EnumConfigEntryHandler;
import uk.co.benjiweber.expressions.function.SeptFunction;

import java.lang.reflect.*;
import java.util.function.*;

public class ExampleConfigEnumFactories implements ConfigFactoryProvider {

    @Override
    public void provideLoadFactories(ImmutableMap.Builder<Type, SeptFunction<String, ConfigEntry, Field, Object, Boolean, Object, Object, ConfigEntryHandler<?>>> immutableBuilder) {
        immutableBuilder.put(ExampleConfigEnum.class, ((id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrOrdinal, defaultEnum) ->
        {
            int enumOrdinal;
            if(enumOrOrdinal instanceof Integer ordinal) {
                enumOrdinal = ordinal;
            }
            else {
                enumOrdinal = ((ExampleConfigEnum) enumOrOrdinal).ordinal();
            }
            return new EnumConfigEntryHandler<ExampleConfigEnum>(id, configEntry, parentField, parentObject, isMultiplayerSynced, enumOrdinal, ((ExampleConfigEnum) defaultEnum).ordinal(), ExampleConfigEnum.class);
        }));
    }

    @Override
    public void provideSaveFactories(ImmutableMap.Builder<Type, Function<Object, Object>> immutableBuilder) {
        immutableBuilder.put(ExampleConfigEnum.class, enumEntry -> enumEntry);
    }
}
