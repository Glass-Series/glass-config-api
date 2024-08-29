package net.glasslauncher.mods.gcapi3.impl.event;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.gcapi3.impl.EventStorage;
import net.glasslauncher.mods.gcapi3.impl.GCCore;
import net.glasslauncher.mods.networking.GlassPacketListener;

import java.util.*;

public class PacketInit implements GlassPacketListener {

    @SuppressWarnings("deprecation")
    @Override
    public void registerGlassPackets() {
        registerGlassPacket(GCCore.NAMESPACE.getMetadata().getId() + ":config_sync", (packet, handler) -> {
            GCCore.log("Got config from server!");
            new ArrayList<>(GCCore.MOD_CONFIGS.keySet()).stream().filter(packet.getNbt()::contains).forEach(namespace -> GCCore.loadServerConfig(namespace, packet.getNbt().getString(namespace))); // oneliner go brrrrrrr

            FabricLoader.getInstance().getEntrypointContainers(GCCore.NAMESPACE.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
                if (EventStorage.POST_LOAD_LISTENERS.containsKey(entrypointContainer.getProvider().getMetadata().getId())) {
                    EventStorage.POST_LOAD_LISTENERS.get(entrypointContainer.getProvider().getMetadata().getId()).getEntrypoint().PostConfigLoaded(EventStorage.EventSource.SERVER_JOIN | EventStorage.EventSource.MODDED_SERVER_JOIN);
                }}));
        },
        true,
        false
        );
    }
}
