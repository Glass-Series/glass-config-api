package net.glasslauncher.mods.gcapi3.mixin.client;

import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Minecraft.class)
public class TranslationMixin {


    @SuppressWarnings("deprecation")
    @Inject(
            method = "init",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;textureManager:Lnet/minecraft/client/texture/TextureManager;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.BY,
                    by = 2
            )
    )
    private void gcapi_hopeThatTranslationsHaveBeenHandledByNow(CallbackInfo ci) {
        AtomicInteger count = new AtomicInteger();
        GCCore.log("Loading config translations.");
        GCCore.MOD_CONFIGS.forEach((identifier, configRootEntry) -> configRootEntry.configCategoryHandler().applyTranslations(count));
        GCCore.log("Loaded " + count.get() + " translations.");
    }
}
