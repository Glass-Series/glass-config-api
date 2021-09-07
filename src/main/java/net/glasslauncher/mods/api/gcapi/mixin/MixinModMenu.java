package net.glasslauncher.mods.api.gcapi.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.ModMenu;
import net.glasslauncher.mods.api.gcapi.GlassConfigAPI;
import net.glasslauncher.mods.api.gcapi.ModMenuAccessor;
import net.minecraft.client.gui.screen.ScreenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

// STATIC ACCESSORS BROKE :CRAB:
@Mixin(ModMenu.class)
public class MixinModMenu {

    @Inject(method = "onInitializeClient", at = @At(target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;", value = "INVOKE", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private void test(CallbackInfo ci, ImmutableMap.Builder<String, Function<ScreenBase, ? extends ScreenBase>> builder) {
        GlassConfigAPI.loadConfigs(builder);
    }


}
