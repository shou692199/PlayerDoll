package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Command.SubCommand;
import me.autobot.playerdoll.InvMenu.Menus.Mainmenu;
import me.autobot.playerdoll.InvMenu.Menus.PlayerSettingmenu;
import me.autobot.playerdoll.PlayerDoll;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class gset extends SubCommand {
    Player player;
    Player doll;
    public gset() {}
    public gset(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);

        if (!checkPermission(sender, dollName, "Gset")) return;
        player = (Player) sender;
        this.doll = Bukkit.getPlayer(dollName);
        if (this.doll == null) return;
        execute();
    }
    @Override
    public void execute() {
        PlayerDoll.getInvManager().openInv(new PlayerSettingmenu(player,doll,null),player);
    }

    @Override
    public ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        return new ArrayList<>(){{addAll(set);}};
    }

    @Override
    public List<Object> tabSuggestion() {
        return new ArrayList<>();
    }
}
