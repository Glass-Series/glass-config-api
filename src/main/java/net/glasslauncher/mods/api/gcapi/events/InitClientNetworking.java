package net.glasslauncher.mods.api.gcapi.events;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.modificationstation.stationapi.api.client.event.network.MultiplayerLogoutEvent;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.network.packet.MessagePacket;
import net.modificationstation.stationapi.api.network.packet.PacketHelper;
import net.modificationstation.stationapi.api.util.Identifier;
import net.modificationstation.stationapi.api.registry.Registry;
import net.modificationstation.stationapi.api.util.Namespace;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.util.ReflectionHelper;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("deprecation")
public class InitClientNetworking {

    @Entrypoint.Namespace
    private final Namespace namespace = Null.get();

    @EventListener
    private void registerNetworkShit(MessageListenerRegistryEvent event) {
        Registry.register(event.registry, Identifier.of(namespace, "config_sync"), (playerBase, message) -> {
            GCCore.log("Got config from server!");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.bytes);
            NbtCompound nbtCompound = NbtIo.readCompressed(byteArrayInputStream);
            new ArrayList<>(GCCore.MOD_CONFIGS.keySet()).stream().map(Identifier::toString).filter(nbtCompound::contains).forEach(namespace -> GCCore.loadServerConfig(namespace, nbtCompound.getString(namespace))); // oneliner go brrrrrrr
        });
        Registry.register(event.registry, Identifier.of(namespace, "ping"), ((playerBase, message) -> PacketHelper.send(new MessagePacket(Identifier.of(namespace, "ping")))));
    }

    @EventListener
    private void onClientDisconnect(MultiplayerLogoutEvent event) {
        GCCore.log("Unloading server synced config!");
        FabricLoader.getInstance().getEntrypointContainers(GCCore.NAMESPACE.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
            try {
                for (Field field : ReflectionHelper.getFieldsWithAnnotation(entrypointContainer.getEntrypoint().getClass(), GConfig.class)) {
                    Identifier configID = Identifier.of(entrypointContainer.getProvider().getMetadata().getId() + ":" + field.getAnnotation(GConfig.class).value());
                    GCCore.loadModConfig(entrypointContainer.getEntrypoint(), entrypointContainer.getProvider(), field, configID, null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
