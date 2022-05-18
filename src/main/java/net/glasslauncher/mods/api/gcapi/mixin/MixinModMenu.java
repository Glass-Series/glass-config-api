package net.glasslauncher.mods.api.gcapi.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.ModMenu;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.minecraft.client.gui.screen.ScreenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(ModMenu.class)
public class MixinModMenu {

    @Inject(method = "onInitializeClient", at = @At(target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", value = "INVOKE", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void hijackConfigScreens(CallbackInfo ci, ImmutableMap.Builder<String, Function<ScreenBase, ? extends ScreenBase>> builder) {
        //noinspection deprecation
        GCCore.log("Adding config screens to ModMenu...");
        Map<String, Function<ScreenBase, ? extends ScreenBase>> map = new HashMap<>();
        //noinspection deprecation
        GCCore.MOD_CONFIGS.forEach((key, value) -> {
            if (!map.containsKey(key.modID.toString()) || value.two().parentField.getAnnotation(GConfig.class).primary()) {
                map.remove(key.modID.toString());
                map.put(key.modID.toString(), (parent) -> value.two().getConfigScreen(parent, value.one()));
            }
        });
        builder.putAll(map);
    }

}
