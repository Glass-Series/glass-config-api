package net.glasslauncher.mods.networking.helpers;

import net.glasslauncher.mods.networking.GlassPacket;
import net.minecraft.entity.player.PlayerEntity;

public abstract class PacketHelperImpl {
    public abstract void send(GlassPacket packet);

    public abstract void sendTo(PlayerEntity player, GlassPacket packet);
}
