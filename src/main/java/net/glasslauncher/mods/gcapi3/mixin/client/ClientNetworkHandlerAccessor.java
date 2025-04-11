package net.glasslauncher.mods.gcapi3.mixin.client;

import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientNetworkHandler.class)
public interface ClientNetworkHandlerAccessor {
    @Accessor
    Connection getConnection();
}
