package net.glasslauncher.mods.api.gcapi.events;

import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.api.gcapi.api.GConfig;
import net.glasslauncher.mods.api.gcapi.impl.GlassConfigAPI;
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
import uk.co.benjiweber.expressions.tuple.BiTuple;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

public class InitClientNetworking {

    @Entrypoint.ModID
    private final ModID modID = Null.get();

    @EventListener
    private void registerNetworkShit(MessageListenerRegistryEvent event) {
        event.registry.register(Identifier.of(modID, "config_sync"), (playerBase, message) -> {
            GlassConfigAPI.log("Got config from server!");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.bytes);
            CompoundTag compoundTag = NBTIO.readGzipped(byteArrayInputStream);
            GlassConfigAPI.log(compoundTag.toString());
            new HashMap<>(GlassConfigAPI.MOD_CONFIGS).keySet().stream().map(modContainerEntrypoint -> modContainerEntrypoint.getMetadata().getId()).filter(compoundTag::containsKey).forEach(modID -> {
                GlassConfigAPI.log(compoundTag.getString(modID));
                GlassConfigAPI.loadServerConfig(modID, compoundTag.getString(modID));
            }); // oneliner go brrrrrrr
        });
        event.registry.register(Identifier.of(modID, "ping"), ((playerBase, message) -> PacketHelper.send(new Message(Identifier.of(modID, "ping")))));
    }

    @EventListener
    private void onClientDisconnect(MultiplayerLogoutEvent event) {
        FabricLoader.getInstance().getEntrypointContainers(GlassConfigAPI.MOD_ID.getMetadata().getId(), Object.class).forEach((entrypointContainer -> {
            try {
                GlassConfigAPI.MOD_CONFIGS.put(entrypointContainer.getProvider(), BiTuple.of(entrypointContainer, null));
                for (Field field : ReflectionHelper.getFieldsWithAnnotation(entrypointContainer.getEntrypoint().getClass(), GConfig.class)) {
                    GlassConfigAPI.loadModConfig(entrypointContainer.getEntrypoint(), entrypointContainer.getProvider(), field, null);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
