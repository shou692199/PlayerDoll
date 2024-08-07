package me.autobot.playerdoll.doll;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class DollData {
    public static final List<DollData> DOLL_DATA_LIST = new CopyOnWriteArrayList<>();
    private final String address;
    private final UUID uuid;
    private final String fullName;
    private final String stripName;
    private ProxiedPlayer dollPlayer;
    private final ServerInfo targetServer;

    public DollData(String address, UUID dollUUID, String dollName, String identifier, UUID callerUUID) {
        this.address = address;
        uuid = dollUUID;
        fullName = dollName;
        if (identifier.isEmpty()) {
            stripName = dollName;
        } else {
            stripName = dollName.substring(1);
        }
        this.targetServer = ProxyServer.getInstance().getPlayer(callerUUID).getServer().getInfo();
    }

    public DollData(String address, UUID dollUUID, String dollName, String identifier, String serverName) {
        this.address = address;
        uuid = dollUUID;
        fullName = dollName;
        if (identifier.isEmpty()) {
            stripName = dollName;
        } else {
            stripName = dollName.substring(1);
        }
        this.targetServer = ProxyServer.getInstance().getServerInfo(serverName);
    }

    public String getAddress() {
        return address;
    }
    public UUID getUuid() {
        return uuid;
    }
    public String getFullName() {
        return fullName;
    }

    public String getStripName() {
        return stripName;
    }
    public ServerInfo getTargetServer() {
        return targetServer;
    }

//    public void setTargetServer(ServerInfo targetServer) {
//        this.targetServer = targetServer;
//    }

    public ProxiedPlayer getDollPlayer() {
        return dollPlayer;
    }

    public void setDollPlayer(ProxiedPlayer dollPlayer) {
        this.dollPlayer = dollPlayer;
    }
}
