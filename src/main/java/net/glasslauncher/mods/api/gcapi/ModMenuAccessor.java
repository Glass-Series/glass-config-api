package net.glasslauncher.mods.api.gcapi;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.ModMenu;
import net.minecraft.client.gui.screen.ScreenBase;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Function;

public interface ModMenuAccessor {

    ImmutableMap<String, Function<ScreenBase, ? extends ScreenBase>> getConfigScreenFactories();
    void setConfigScreenFactories(ImmutableMap<String, Function<ScreenBase, ? extends ScreenBase>> map);
}
