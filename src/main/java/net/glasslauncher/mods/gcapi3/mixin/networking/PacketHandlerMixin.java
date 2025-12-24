package net.glasslauncher.mods.gcapi3.mixin.networking;

import net.glasslauncher.mods.networking.GlassNetworkHandler;
import net.minecraft.network.NetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(NetworkHandler.class)
public class PacketHandlerMixin implements GlassNetworkHandler {
    @Unique
    private boolean hasGlassNetworking = false;

    @Override
    public boolean glass_Networking$hasGlassNetworking() {
        return hasGlassNetworking;
    }

    @Override
    public void glass_Networking$setHasGlassNetworking() {
        hasGlassNetworking = true;
    }
}
