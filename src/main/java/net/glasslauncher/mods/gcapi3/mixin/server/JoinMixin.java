package net.glasslauncher.mods.gcapi3.mixin.server;

import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.networking.GlassNetworkHandler;
import net.glasslauncher.mods.networking.GlassNetworking;
import net.glasslauncher.mods.networking.GlassPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.login.LoginHelloPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLoginNetworkHandler.class)
public class JoinMixin {

    @SuppressWarnings("deprecation")
    @Inject(
            method = "accept",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/ServerPlayerEntity;method_317()V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void gcapi_afterLogin(LoginHelloPacket arg, CallbackInfo ci, ServerPlayerEntity player) {
        if (((GlassNetworkHandler) player.field_255).glass_Networking$hasGlassNetworking()) {
            GCCore.log("Ping successful! Sending config to " + player.name);
//            GCAPI_PLAYERS.put(player, true); // There's no real need for this, I think?
            NbtCompound nbtCompound = new NbtCompound();
            GCCore.exportConfigsForServer(nbtCompound);
            GlassPacket configSync = new GlassPacket(GCCore.NAMESPACE.getMetadata().getId(), "config_sync", nbtCompound);
            GlassNetworking.sendToPlayer(player, configSync);
        }
    }
}
