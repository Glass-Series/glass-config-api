package net.glasslauncher.mods.gcapi3.mixin.networking;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.glasslauncher.mods.networking.GlassPacket;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Connection.class)
class ConnectionMixin {
    @Unique
    private static final Object GLASSNETWORKING$PACKET_READ_LOCK = new Object();
    @Unique
    private static final AtomicBoolean GLASSNETWORKING$BLOCKNG_PACKET = new AtomicBoolean();

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/NetworkHandler;)V"
            )
    )
    private void stationapi_ifIdentifiable(Packet instance, NetworkHandler networkHandler, Operation<Void> original) {
        if (instance instanceof GlassPacket glassPacket) {
            if (glassPacket.blocking) {
                synchronized (GLASSNETWORKING$PACKET_READ_LOCK) {
                    GLASSNETWORKING$BLOCKNG_PACKET.set(false);
                    GLASSNETWORKING$PACKET_READ_LOCK.notifyAll();
                }
            }
        } else original.call(instance, networkHandler);
    }

    @Inject(
            method = "read",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void stationapi_waitOnBlockingPacket(
            CallbackInfoReturnable<Boolean> cir,
            @Local(index = 2) Packet packet
    ) {
        if (!(packet instanceof GlassPacket glassPacket) || !glassPacket.blocking) return;
        synchronized (GLASSNETWORKING$PACKET_READ_LOCK) {
            GLASSNETWORKING$BLOCKNG_PACKET.set(true);
            while (GLASSNETWORKING$BLOCKNG_PACKET.get()) try {
                GLASSNETWORKING$PACKET_READ_LOCK.wait();
            } catch (InterruptedException ignored) {}
        }
    }
}
