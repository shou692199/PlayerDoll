package me.autobot.playerdoll.Command.SubCommands;

import me.autobot.playerdoll.Command.ArgumentType;
import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.Configs.ConfigManager;
import me.autobot.playerdoll.Configs.LangFormatter;
import me.autobot.playerdoll.Configs.PermissionManager;
import me.autobot.playerdoll.Dolls.DollHelper;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;


public class Spawn extends SubCommand {

    public Spawn(Player sender, String dollName, String[] args) {
        super(sender, dollName);

        int serverMaxDoll = ConfigManager.getConfig().getInt("Global.ServerMaxDoll");
        if (serverMaxDoll > -1 && !sender.isOp() && PlayerDoll.dollManagerMap.size() >= serverMaxDoll) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("DollCapacityIsFull",'&'));
            return;
        }
        String ownerUUID = dollConfig.getString("Owner.UUID");
        Player owner = Bukkit.getPlayer(UUID.fromString(ownerUUID));
        if (owner != null) {
            PermissionManager perm = PermissionManager.getInstance(owner);
            if (!perm.groupName.equals(permissionManager.groupName)) {
                permissionManager = perm;
                dollConfig.set("Owner.Perm",perm.groupName);
                dollYAML.saveConfig();
            }
        }

        Map<String, Object> map = dollConfig.getConfigurationSection("setting").getValues(false);
        permissionManager.flagGlobalToggles.forEach(map::putIfAbsent);

        if (!player.isOp()) {
            int count = 0;
            for (IDoll d : PlayerDoll.dollManagerMap.values()) {
                if (d.getConfigManager().config.getString("Owner.UUID").equals(ownerUUID)) {
                    count++;
                }
            }
            int maxDollSpawn = permissionManager.maxDollSpawn;
            if (count >= maxDollSpawn) {
                player.sendMessage(LangFormatter.YAMLReplaceMessage("PlayerSpawnTooMuchDoll", '&', new Pair<>("%a%", Integer.toString(maxDollSpawn))));
                return;
            }
        }
        dollConfig = dollYAML.reloadConfig().getConfig();
        if (!player.isOp() && !permissionManager.bypassMaxPlayer && Bukkit.getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
            player.sendMessage(LangFormatter.YAMLReplaceMessage("ServerReachMaxPlayer", '&'));
            return;
        }
        try {
            PlayerDoll.dollManagerMap.put(dollName, null);
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit."+ PlayerDoll.version +".entity.CraftPlayer");
            ServerPlayer serverPlayer = (ServerPlayer) player.getClass().asSubclass(craftPlayerClass).getDeclaredMethod("getHandle").invoke(player);
            IDoll doll = (IDoll) DollHelper.callSpawn(serverPlayer,dollName, PlayerDoll.version);
            if (doll != null) {
                PlayerDoll.dollManagerMap.put(dollName, doll);
                if (args != null && args.length > 0 && checkArgumentValid(ArgumentType.ALIGN_IN_GRID,args[0])) {
                    var pos = player.getLocation();
                    doll._setPos(Math.round(pos.getX() * 2) / 2.0, pos.getBlockY(), Math.round(pos.getZ() * 2) / 2.0);
                }
            } else PlayerDoll.dollManagerMap.remove(dollName);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}