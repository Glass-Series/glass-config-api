package net.glasslauncher.mods.gcapi3.mixin.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.glasslauncher.mods.networking.GlassNetworking;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.handshake.HandshakePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

@Mixin(HandshakePacket.class)
public class HandshakePacketMixin {
    @Shadow
    public String name;

    @ModifyConstant(
            method = "read",
            constant = @Constant(intValue = 32)
    )
    private int glassnetworking_stopClientFromDying(int constant) {
        return Short.MAX_VALUE;
    }

    @Inject(
            method = "read",
            at = @At("TAIL")
    )
    @Environment(EnvType.CLIENT)
    private void glassnetworking_markAsNetworked(DataInputStream par1, CallbackInfo ci) {
        String[] names = name.split(";");
        //noinspection deprecation
        GlassNetworking.setServerHasNetworking(Arrays.asList(names).contains("glassnetworking"));
    }

    @Inject(
            method = "apply",
            at = @At("HEAD")
    )
    private void glassnetworking_stopClientFromDying(NetworkHandler par1, CallbackInfo ci) {
        String[] names = name.split(";");
        name = names[0];
    }

    @Inject(
            method = "write",
            at = @At("HEAD")
    )
    @Environment(EnvType.CLIENT)
    private void glassnetworking_injectGlassNetworkingFlag(DataOutputStream par1, CallbackInfo ci) {
        this.name += ";glassnetworking;";
    }
}
