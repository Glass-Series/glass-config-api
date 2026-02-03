package net.glasslauncher.mods.gcapi3.mixin.client;

import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.net.SocketAddress;

@Mixin(Connection.class)
public interface ConnectionAccessor {
    @Accessor("address")
    SocketAddress getAddress();
}
