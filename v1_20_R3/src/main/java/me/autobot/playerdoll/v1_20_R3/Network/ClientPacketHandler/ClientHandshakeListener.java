package me.autobot.playerdoll.v1_20_R3.Network.ClientPacketHandler;

import com.mojang.authlib.GameProfile;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.v1_20_R3.Network.CursedConnections;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.login.*;

import java.util.logging.Level;

public class ClientHandshakeListener implements ClientLoginPacketListener {

    private final Connection connection;
    public ClientHandshakeListener(Connection connection) {
        this.connection = connection;
        this.connection.channel.attr(Connection.ATTRIBUTE_CLIENTBOUND_PROTOCOL).set(ConnectionProtocol.LOGIN.codec(PacketFlow.CLIENTBOUND));
    }

    @Override
    public void handleHello(ClientboundHelloPacket clientboundHelloPacket) {

    }

    @Override
    public void handleGameProfile(ClientboundGameProfilePacket clientboundGameProfilePacket) {
        // Handle This Then Go To Configuration State
        if (!PlayerDoll.isFolia) {
            PacketUtils.ensureRunningOnSameThread(clientboundGameProfilePacket, this, CursedConnections.server);
        }
        GameProfile gameProfile = clientboundGameProfilePacket.getGameProfile();
        PlayerDoll.getPluginLogger().log(Level.INFO,"Fake Client Handle Client GameProfile");
        this.connection.send(new ServerboundLoginAcknowledgedPacket());
        this.connection.setListener(new ClientConfigurationListener(this.connection, gameProfile));
    }

    @Override
    public void handleDisconnect(ClientboundLoginDisconnectPacket clientboundLoginDisconnectPacket) {
        this.connection.disconnect(clientboundLoginDisconnectPacket.getReason());
    }

    @Override
    public void handleCompression(ClientboundLoginCompressionPacket clientboundLoginCompressionPacket) {
        PlayerDoll.getPluginLogger().log(Level.INFO,"Setup Compression for Fake Client");
        this.connection.setupCompression(clientboundLoginCompressionPacket.getCompressionThreshold(),true);
    }

    @Override
    public void handleCustomQuery(ClientboundCustomQueryPacket clientboundCustomQueryPacket) {

    }

    @Override
    public void onDisconnect(Component component) {
        connection.disconnect(component);
    }

    @Override
    public boolean isAcceptingMessages() {
        return connection.isConnected();
    }

}
