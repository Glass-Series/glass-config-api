package net.glasslauncher.mods.api.gcapi.events;

import net.glasslauncher.mods.api.gcapi.impl.GlassConfigAPI;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.util.io.CompoundTag;
import net.minecraft.util.io.NBTIO;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.util.Null;

import java.io.ByteArrayInputStream;

public class InitClientNetworking {

    @Entrypoint.ModID
    private final ModID modID = Null.get();

    @EventListener
    private void registerNetworkShit(MessageListenerRegistryEvent event) {
        event.registry.register(Identifier.of(modID, "config_sync"), (playerBase, message) -> {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.bytes);
            CompoundTag compoundTag = NBTIO.readGzipped(byteArrayInputStream);
            GlassConfigAPI.MOD_CONFIGS.keySet().stream().map(modContainerEntrypoint -> modContainerEntrypoint.getMetadata().getId()).filter(compoundTag::containsKey).forEach(modID -> GlassConfigAPI.loadServerConfig(modID, compoundTag.getString(modID))); // oneliner go brrrrrrr
        });
        event.registry.register(Identifier.of(modID, "ping"), ((playerBase, message) -> PacketHelper.send(new Message(Identifier.of(modID, "ping")))));
    }
}
