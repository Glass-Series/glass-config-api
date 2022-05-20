package net.glasslauncher.mods.api.gcapi.events;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.impl.GCCore;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.NBTIO;
import net.modificationstation.stationapi.api.client.event.network.MultiplayerLogoutEvent;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;
import net.modificationstation.stationapi.api.util.ReflectionHelper;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class InitClientNetworking {

    @Entrypoint.ModID
    private final ModID modID = Null.get();

    @EventListener
    private void registerNetworkShit(MessageListenerRegistryEvent event) {
        event.registry.register(Identifier.of(modID, "config_sync"), (playerBase, message) -> {
            GCCore.log("Got config from server!");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.bytes);
            CompoundTag compoundTag = NBTIO.readGzipped(byteArrayInputStream);
            new ArrayList<>(GCCore.MOD_CONFIGS.keySet()).stream().map(Identifier::toString).filter(compoundTag::containsKey).forEach(modID -> GCCore.loadServerConfig(modID, compoundTag.getString(modID))); // oneliner go brrrrrrr
        });
        event.registry.register(Identifier.of(modID, "ping"), ((playerBase, message) -> PacketHelper.send(new Message(Identifier.of(modID, "ping")))));
    }

    @EventListener
    private void onClientDisconnect(MultiplayerLogoutEvent event) {
        GCCore.log("Unloading server synced config!");
        FabricLoader.getInstance().getEntrypointContainers(GCCore.MOD_ID.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
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
