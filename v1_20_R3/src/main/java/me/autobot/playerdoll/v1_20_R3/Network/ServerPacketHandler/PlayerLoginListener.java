package me.autobot.playerdoll.v1_20_R3.Network.ServerPacketHandler;

import me.autobot.playerdoll.v1_20_R3.player.TransformPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

import java.lang.reflect.Field;

public class PlayerLoginListener extends ServerLoginPacketListenerImpl {
    private final MinecraftServer server;
    private final ServerPlayer player;
    private static Field serverPlayerField;

    static {
        for (Field field : ServerLoginPacketListenerImpl.class.getDeclaredFields()) {
            if (field.getType() == ServerPlayer.class) {
                field.setAccessible(true);
                serverPlayerField = field;
            }
        }
    }
    public PlayerLoginListener(MinecraftServer minecraftserver, Connection networkmanager, ServerPlayer player) {
        super(minecraftserver, networkmanager);
        this.server = minecraftserver;
        this.player = player;
    }

    @Override
    public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket serverboundloginacknowledgedpacket) {
        CommonListenerCookie commonlistenercookie = CommonListenerCookie.createInitial(player.getGameProfile());
        ServerPlayer serverPlayer = new TransformPlayer(player.getBukkitEntity());
        ServerConfigurationPacketListenerImpl serverconfigurationpacketlistenerimpl = new ServerConfigurationPacketListenerImpl(this.server, this.connection, commonlistenercookie, serverPlayer);
        this.connection.setListener(serverconfigurationpacketlistenerimpl);
        serverconfigurationpacketlistenerimpl.startConfiguration();

    }
    public static ServerPlayer getPlayer(ServerLoginPacketListenerImpl instance) {
        if (serverPlayerField == null) {
            return null;
        }
        serverPlayerField.setAccessible(true);
        try {
            return (ServerPlayer) serverPlayerField.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
