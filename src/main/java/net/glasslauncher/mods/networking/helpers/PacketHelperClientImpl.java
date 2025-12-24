package net.glasslauncher.mods.networking.helpers;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.networking.GlassPacket;
import net.glasslauncher.mods.networking.helpers.PacketHelperImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class PacketHelperClientImpl extends PacketHelperImpl {
    @Override
    public void send(GlassPacket packet) {
        //noinspection deprecation
        Minecraft minecraft = (Minecraft) FabricLoader.getInstance().getGameInstance();
        if (minecraft.world.isRemote)
            minecraft.getNetworkHandler().sendPacket(packet);
        else packet.apply(null);
    }

    @Override
    public void sendTo(PlayerEntity player, GlassPacket packet) {
        if (!player.world.isRemote)
            packet.apply(null);
    }
}
