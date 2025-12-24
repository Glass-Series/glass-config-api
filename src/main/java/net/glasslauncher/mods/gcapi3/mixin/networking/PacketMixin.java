package net.glasslauncher.mods.gcapi3.mixin.networking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.glasslauncher.mods.networking.GlassNetworking;
import net.glasslauncher.mods.networking.GlassPacket;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The big cheese of the mod. Stolen from StAPI, and should be fully compatible.
 */
@Mixin(Packet.class)
abstract class PacketMixin {
    @Shadow
    static void register(int rawId, boolean clientBound, boolean serverBound, Class<? extends Packet> type) {}

    @Shadow
    public static void writeString(String string, DataOutputStream dataOutputStream) {}

    @Shadow
    public static String readString(DataInputStream dataInputStream, int i) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/Packet;register(IZZLjava/lang/Class;)V", ordinal = 56, shift = At.Shift.AFTER))
    private static void glassnetworking_afterVanillaPackets(CallbackInfo ci) {
        register(GlassNetworking.PACKET_ID, true, true, GlassPacket.class);
    }

    @Inject(
            method = "getRawId",
            at = @At("HEAD"),
            cancellable = true
    )
    private void glassnetworking_ifIdentifiable(CallbackInfoReturnable<Integer> cir) {
        //noinspection DataFlowIssue
        if (((Packet) (Object) this) instanceof GlassPacket)
            cir.setReturnValue(GlassNetworking.PACKET_ID);
    }

    @Inject(
            method = "write(Lnet/minecraft/network/packet/Packet;Ljava/io/DataOutputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/Packet;write(Ljava/io/DataOutputStream;)V"
            )
    )
    private static void glassnetworking_ifIdentifiable(Packet packet, DataOutputStream out, CallbackInfo ci) {
        if (packet instanceof GlassPacket idPacket)
            writeString(idPacket.getFullId(), out);
    }

    @WrapOperation(
            method = "read(Ljava/io/DataInputStream;Z)Lnet/minecraft/network/packet/Packet;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/Packet;create(I)Lnet/minecraft/network/packet/Packet;"
            )
    )
    private static Packet glassnetworking_ifIdentifiable(int rawId, Operation<Packet> original, @Local(argsOnly = true) DataInputStream in, @Local(argsOnly = true) boolean server) throws IOException {
        if (rawId == GlassNetworking.PACKET_ID) {
            String identifier = readString(in, Short.MAX_VALUE);
            if (
                    server && !GlassNetworking.isServerPacket(identifier) ||
                            !server && !GlassNetworking.isClientPacket(identifier)
            ) {
                if (identifier == null || !identifier.endsWith("_optional")) {
                    throw new IOException("Bad packet id " + identifier);
                }
            }
            return new GlassPacket();
        }
        return original.call(rawId);
    }
}
