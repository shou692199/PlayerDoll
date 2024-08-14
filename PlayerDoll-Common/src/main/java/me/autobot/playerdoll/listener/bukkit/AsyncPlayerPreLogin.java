package me.autobot.playerdoll.listener.bukkit;

import io.netty.channel.Channel;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.config.BasicConfig;
import me.autobot.playerdoll.doll.PlayerConvertInjector;
import me.autobot.playerdoll.netty.ConnectionFetcher;
import me.autobot.playerdoll.netty.DollConnection;
import me.autobot.playerdoll.scheduler.SchedulerHelper;
import me.autobot.playerdoll.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Function;

public class AsyncPlayerPreLogin implements Listener {

    private static final Class<?> loginListenerClass = ReflectionUtil.getClass("net.minecraft.server.network.LoginListener");
    // Just trigger the static block
    private static final Class<?> playerLoginListenerClass = ReflectionUtil.getPluginNMSClass("connection.login.PlayerLoginListener");
    public static Function<Object, Boolean> checkProtocol;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (DollConnection.DOLL_CONNECTIONS.containsKey(event.getUniqueId())) {
            SchedulerHelper.scheduler.globalTask(() -> Bukkit.getOfflinePlayer(event.getUniqueId()).setOp(true));
            return;
        }
        if (!BasicConfig.get().convertPlayer.getValue()) {
            return;
        }
        String address = event.getAddress().getHostAddress();
        try {
            // Prevent player logging-in too fast
            // Delay a bit to let the ConnectionList to update
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<Object> connectionList = ConnectionFetcher.getServerConnectionList();
        synchronized (connectionList) {
            for (Object connections : connectionList) {
                Channel channel = ConnectionFetcher.getChannel(connections);
                if (channel == null) {
                    continue;
                }
                // Only IP are provided from the event. Not sure 100% match
                if (!((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress().equals(address)) {
                    continue;
                }
                Object packetListener = ConnectionFetcher.getPacketListener(connections);
                if (loginListenerClass.equals(packetListener.getClass())) {
                    if (checkProtocol.apply(packetListener)) {
                        if (channel.pipeline().get("packet_handler") != null && channel.pipeline().get("player_convert_injector") == null) {
                            PlayerDoll.LOGGER.info("Capture Player Connection");
                            new PlayerConvertInjector(connections, channel);
                            break;
                        }
                    }
                }
            }
        }
    }
}
