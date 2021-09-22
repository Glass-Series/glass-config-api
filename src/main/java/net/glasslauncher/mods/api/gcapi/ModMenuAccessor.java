package net.glasslauncher.mods.api.gcapi;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.screen.ScreenBase;

import java.util.function.Function;

public interface ModMenuAccessor {

    ImmutableMap<String, Function<ScreenBase, ? extends ScreenBase>> getConfigScreenFactories();
    void setConfigScreenFactories(ImmutableMap<String, Function<ScreenBase, ? extends ScreenBase>> map);
}
