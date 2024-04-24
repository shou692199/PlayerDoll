package me.autobot.playerdoll.v1_20_R4.Network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.logging.Level;

public class CursedClientConnection extends Connection {
    public CursedClientConnection() {
        super(PacketFlow.CLIENTBOUND);
    }

    public static CursedClientConnection connectToServer(InetSocketAddress serverAddress) {
        final CursedClientConnection connection = new CursedClientConnection();
        ChannelFuture future = Connection.connect(serverAddress, false,connection);
        future.syncUninterruptibly();
        return connection;
    }

    @Override
    public void disconnect(Component ichatbasecomponent) {
        PlayerDoll.getPluginLogger().log(Level.INFO, "Client Disconnected, "+ ichatbasecomponent.getString());
        super.disconnect(ichatbasecomponent);
    }
/*
    @Override
    public void send(Packet<?> packet) {
        System.out.println("Client Send: " + packet);
        super.send(packet);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client Read: "+msg);
        super.channelRead(ctx, msg);
    }

 */

}
