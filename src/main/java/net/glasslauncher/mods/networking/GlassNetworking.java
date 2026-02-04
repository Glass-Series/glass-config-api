package net.glasslauncher.mods.networking;

import com.google.common.hash.Hashing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.glasslauncher.mods.gcapi3.mixin.client.MinecraftAccessor;
import net.glasslauncher.mods.gcapi3.mixin.networking.accessor.NbtElementAccessor;
import net.glasslauncher.mods.networking.GlassNetworkHandler;
import net.glasslauncher.mods.networking.GlassPacket;
import net.glasslauncher.mods.networking.GlassPacketListener;
import net.glasslauncher.mods.networking.helpers.PacketHelperClientImpl;
import net.glasslauncher.mods.networking.helpers.PacketHelperImpl;
import net.glasslauncher.mods.networking.helpers.PacketHelperServerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.ClientNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.NetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;

public class GlassNetworking implements ModInitializer {
    public static final long MASK = Hashing.sipHash24().hashUnencodedChars("glassnetworking").asLong();
    public static final int PACKET_ID = 253; // StAPI uses 254, and 255 is the disconnect packet
    public static final Logger LOGGER = LogManager.getLogger("GlassNetworking|Mod");

    private static boolean serverHasNetworking = false;

    private static final PacketHelperImpl PACKET_HELPER = FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT) ? new PacketHelperClientImpl() : new PacketHelperServerImpl();

    static final ArrayList<String> SERVER_BOUND_PACKETS = new ArrayList<>();
    static final ArrayList<String> CLIENT_BOUND_PACKETS = new ArrayList<>();
    static final HashMap<String, BiConsumer<GlassPacket, NetworkHandler>> PACKET_HANDLERS = new HashMap<>();

    public static int writeAndGetNbtLength(NbtElement element, OutputStream dataOutput) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(dataOutput);
        ((NbtElementAccessor)element).invokeWrite(outputStream);
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.size();
    }

    public static boolean isClientPacket(String fullId) {
        return CLIENT_BOUND_PACKETS.contains(fullId);
    }

    public static boolean isServerPacket(String fullId) {
        return SERVER_BOUND_PACKETS.contains(fullId);
    }

    /**
     * If this is called on server, the packet will be applied as if the server just got the packet from a client.
     * If this is called on a client that's in singleplayer, the client will apply the packet as if it was the server.
     */
    public static void sendToServer(GlassPacket packet) {
        PACKET_HELPER.send(packet);
    }

    /**
     * If this is called in client, it will treat the packet as if it got it from a server.
     */
    public static void sendToPlayer(PlayerEntity player, GlassPacket packet) {
        PACKET_HELPER.sendTo(player, packet);
    }

    @Override
    public void onInitialize() {
        FabricLoader.getInstance().getEntrypoints("glassnetworking", GlassPacketListener.class).forEach(GlassPacketListener::registerGlassPackets);
        LOGGER.info("Registered {} packets, of which {} are client bound, and {} are server bound.", PACKET_HANDLERS.size(), CLIENT_BOUND_PACKETS.size(), SERVER_BOUND_PACKETS.size());
    }

    /**
     * If the server has this mod installed, this will return true.
     */
    @Environment(EnvType.CLIENT)
    public static boolean serverHasNetworking() {
        ClientNetworkHandler handler = MinecraftAccessor.getInstance().getNetworkHandler();
        if (handler == null) {
            return false;
        }

        return serverHasNetworking;
    }

    @Environment(EnvType.CLIENT)
    @ApiStatus.Internal
    @Deprecated
    public static void setServerHasNetworking(boolean hasNetworking) {
        serverHasNetworking = hasNetworking;
    }

    /**
     * If the player has this mod installed, this will return true.
     */
    @Environment(EnvType.SERVER)
    public static boolean clientHasNetworking(ServerPlayerEntity entity) {
        return ((GlassNetworkHandler) entity.networkHandler).glass_Networking$hasGlassNetworking();
    }
}
