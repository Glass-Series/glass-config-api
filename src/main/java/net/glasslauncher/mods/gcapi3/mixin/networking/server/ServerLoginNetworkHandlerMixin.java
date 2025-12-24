package net.glasslauncher.mods.gcapi3.mixin.networking.server;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.glasslauncher.mods.networking.GlassNetworkHandler;
import net.minecraft.network.packet.handshake.HandshakePacket;
import net.minecraft.network.packet.login.LoginHelloPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ServerLoginNetworkHandler.class)
public class ServerLoginNetworkHandlerMixin {
    @Inject(
            method = "onHandshake",
            at = @At("HEAD")
    )
    private void glassnetworking_handleLogin(HandshakePacket par1, CallbackInfo ci) {
        if (Arrays.asList(par1.name.split(";")).contains("glassnetworking")) {
            ((GlassNetworkHandler) this).glass_Networking$setHasGlassNetworking();
        }
    }

    @WrapOperation(method = "onHandshake", at = @At(value = "NEW", target = "(Ljava/lang/String;)Lnet/minecraft/network/packet/handshake/HandshakePacket;"))
    private HandshakePacket glassnetworking_tellThemIMod(String s, Operation<HandshakePacket> original, @Local(argsOnly = true) HandshakePacket handshakePacket) {
        if (Arrays.asList(handshakePacket.name.split(";")).contains("glassnetworking")) {
            s += ";glassnetworking;";
        }
        return original.call(s);
    }

    @Inject(
            method = "accept",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/login/LoginHelloPacket;<init>(Ljava/lang/String;IJB)V",
                    shift = At.Shift.AFTER
            )
    )
    private void stationapi_checkModded(LoginHelloPacket arg, CallbackInfo ci, @Local(ordinal = 0) ServerPlayNetworkHandler var5) {
        GlassNetworkHandler moddedPacketHandler = ((GlassNetworkHandler) this);
        if (moddedPacketHandler.glass_Networking$hasGlassNetworking()) {
            ((GlassNetworkHandler) var5).glass_Networking$setHasGlassNetworking();
        }
    }
}
