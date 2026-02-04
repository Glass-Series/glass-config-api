package net.glasslauncher.mods.gcapi3.mixin.client;

import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigCategoryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.networking.GlassNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.ClientWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public World world;

    @Inject(method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ClientPlayerEntity;teleportTop()V"))
    private void checkVanillaJoined(World string, String playerEntity, PlayerEntity par3, CallbackInfo ci) {
        if(!GlassNetworking.serverHasNetworking() && world instanceof ClientWorld) {
            //noinspection deprecation
            GCCore.MOD_CONFIGS.forEach((identifier, configRootEntry) -> recursiveTriggerVanillaBehavior(configRootEntry.configCategoryHandler()));
        }
    }

    @Unique
    private void recursiveTriggerVanillaBehavior(ConfigCategoryHandler configCategory) {
        configCategory.values.forEach((aClass, configBase) -> {
            if(configBase.getClass().isAssignableFrom(ConfigCategoryHandler.class)) {
                recursiveTriggerVanillaBehavior((ConfigCategoryHandler) configBase);
            }
            else {
                ((ConfigEntryHandler<?>) configBase).vanillaServerBehavior();
            }
        });
    }
}
