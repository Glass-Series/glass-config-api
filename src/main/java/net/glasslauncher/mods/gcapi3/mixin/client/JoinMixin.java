package net.glasslauncher.mods.gcapi3.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.gcapi3.api.ConfigRoot;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigCategoryHandler;
import net.glasslauncher.mods.gcapi3.impl.object.ConfigEntryHandler;
import net.glasslauncher.mods.networking.GlassNetworking;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.handshake.HandshakePacket;
import net.minecraft.network.packet.play.DisconnectPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@SuppressWarnings("deprecation")
@Mixin(ClientNetworkHandler.class)
public class JoinMixin {
    @Inject(
            method = "onHandshake",
            at = @At("HEAD")
    )
    private void onLoginSuccess(HandshakePacket par1, CallbackInfo ci) {
    }

    @Unique
    private void recursiveTriggerVanillaUndoBehavior(ConfigCategoryHandler configCategory) {
        configCategory.values.forEach((aClass, configBase) -> {
            if(configBase.getClass().isAssignableFrom(ConfigCategoryHandler.class)) {
                recursiveTriggerVanillaUndoBehavior((ConfigCategoryHandler) configBase);
            }
            else {
                ((ConfigEntryHandler<?>) configBase).undoVanillaServerBehaviour();
            }
        });
    }

    @Inject(
            method = "onDisconnected",
            at = @At("HEAD")
    )
    private void onDropped(String reason, Object[] stacktrace, CallbackInfo ci) {
        onClientDisconnect();
    }

    @Inject(
            method = "onDisconnect",
            at = @At("HEAD")
    )
    private void onKicked(DisconnectPacket packet, CallbackInfo ci) {
        onClientDisconnect();
    }

    @Inject(
            method = "sendPacketAndDisconnect",
            at = @At("HEAD")
    )
    private void onDisconnect(Packet par1, CallbackInfo ci) {
        onClientDisconnect();
    }

    @Unique
    private void onClientDisconnect() {
        GCCore.log("Unloading server synced config!");
        if(!GlassNetworking.serverHasNetworking()) {
            GCCore.MOD_CONFIGS.forEach((identifier, configRootEntry) -> recursiveTriggerVanillaUndoBehavior(configRootEntry.configCategoryHandler()));
        }
        FabricLoader.getInstance().getEntrypointContainers(GCCore.NAMESPACE.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
            try {
                for (Field field : entrypointContainer.getEntrypoint().getClass().getDeclaredFields()) {
                    if (field.getAnnotation(ConfigRoot.class) == null) {
                        continue;
                    }
                    String configID = entrypointContainer.getProvider().getMetadata().getId() + ":" + field.getAnnotation(ConfigRoot.class).value();
                    GCCore.loadModConfig(entrypointContainer.getEntrypoint(), entrypointContainer.getProvider(), field, configID, null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
