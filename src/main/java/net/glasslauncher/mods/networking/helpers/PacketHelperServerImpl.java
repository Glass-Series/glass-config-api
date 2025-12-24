package net.glasslauncher.mods.networking.helpers;

import net.glasslauncher.mods.networking.GlassPacket;
import net.glasslauncher.mods.networking.helpers.PacketHelperImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PacketHelperServerImpl extends PacketHelperImpl {
    @Override
    public void send(GlassPacket packet) {
        packet.apply(null);
    }

    @Override
    public void sendTo(PlayerEntity player, GlassPacket packet) {
        ((ServerPlayerEntity) player).field_255.method_835(packet);
    }
}
