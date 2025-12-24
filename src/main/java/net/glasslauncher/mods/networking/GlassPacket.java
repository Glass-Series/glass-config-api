package net.glasslauncher.mods.networking;

import lombok.Getter;
import net.glasslauncher.mods.gcapi3.mixin.networking.accessor.NbtCompoundAccessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.ApiStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class GlassPacket extends Packet {
    private @Getter String modId;
    private @Getter String packetId;
    private @Getter NbtCompound nbt;

    // Don't remove from here. Bad. Spamming logs is a bad idea.
    public static final ArrayList<String> LOGGED_INVALID_IDS = new ArrayList<>() {
        @Override
        public boolean remove(Object o) {
            throw new RuntimeException("Don't try to remove from the invalid ids list!");
        }

        @Override
        public String remove(int index) {
            throw new RuntimeException("Don't try to remove from the invalid ids list!");
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            throw new RuntimeException("Don't try to remove from the invalid ids list!");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new RuntimeException("Don't try to remove from the invalid ids list!");
        }

        @Override
        public boolean removeIf(Predicate<? super String> filter) {
            throw new RuntimeException("Don't try to remove from the invalid ids list!");
        }
    };

    private int length;

    /***
     * Use this when sending the packet.
     */
    public <T extends GlassPacket> GlassPacket(String modId, String packetId, NbtCompound nbt) {
        this.modId = modId;
        this.packetId = packetId;
        this.nbt = nbt;
    }

    /**
     * Don't use. Internal IMPL constructor.
     */
    @ApiStatus.Internal
    public GlassPacket() {}

    @Override
    public void read(DataInputStream stream) {
        nbt = new NbtCompound();
        ((NbtCompoundAccessor)nbt).invokeRead(stream);
        packetId = nbt.getString("glassnetworking:packetId");
        modId = nbt.getString("glassnetworking:modId");
    }

    @Override
    public void write(DataOutputStream stream) {
        nbt.putString("glassnetworking:packetId", packetId);
        nbt.putString("glassnetworking:modId", modId);
        length = net.glasslauncher.mods.networking.GlassNetworking.writeAndGetNbtLength(nbt, stream);
    }

    @Override
    public void apply(NetworkHandler networkHandler) {
        BiConsumer<GlassPacket, NetworkHandler> packetHandler = net.glasslauncher.mods.networking.GlassNetworking.PACKET_HANDLERS.get(getFullId());
        if (packetHandler != null) {
            packetHandler.accept(this, networkHandler);
        }
        else if (!LOGGED_INVALID_IDS.contains(getFullId())) {
            GlassNetworking.LOGGER.warn("Got packet {} which has no handler! Suppressing future warnings.", getFullId());
            LOGGED_INVALID_IDS.add(getFullId());
        }
    }

    @Override
    public int size() {
        return length;
    }

    public String getFullId() {
        return modId + ":" + packetId;
    }
}
