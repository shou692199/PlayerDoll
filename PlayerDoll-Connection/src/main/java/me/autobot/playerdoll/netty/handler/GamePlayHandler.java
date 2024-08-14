package me.autobot.playerdoll.netty.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.autobot.playerdoll.netty.packet.ClientPackets;
import me.autobot.playerdoll.netty.packet.ServerPackets;
import me.autobot.playerdoll.scheduler.SchedulerHelper;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GamePlayHandler extends ChannelDuplexHandler {

    private final UUID uuid;
    private int lastAcceptedId = -1;

    public GamePlayHandler(UUID uuid) {
        this.uuid = uuid;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (ClientPackets.playerPositionPacketClass.isInstance(msg)) {
            int id = ClientPackets.getPlayerPositionPacketId(msg);
            if (lastAcceptedId != id) {
                lastAcceptedId = id;
                channel.writeAndFlush(ServerPackets.createAcceptTeleportPacket(id));
            }
        } else if (ClientPackets.deathScreenPacketClass.isInstance(msg)) {
            channel.writeAndFlush(ServerPackets.createPerformRespawnPacket());
            SchedulerHelper.scheduler.entityTaskDelayed(channel::close, Bukkit.getPlayer(uuid), 6);
        } else if (ClientPackets.gameEventPacketClass.isInstance(msg)) {
            channel.writeAndFlush(ServerPackets.createPerformRespawnPacket());
        }
        super.channelRead(ctx, msg);
    }


}
