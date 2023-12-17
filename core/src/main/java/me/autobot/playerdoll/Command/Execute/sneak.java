package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class sneak extends SubCommand {
    Player player;
    Player dollPlayer;
    IDoll doll;
    String[] args;
    public sneak() {}
    public sneak(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Sneak")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        dollPlayer = Bukkit.getPlayer(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        doll.getActionPack().setSneaking(args[0] == null ? !dollPlayer.isSneaking() : Boolean.parseBoolean(args[0]));
    }
    @Override
    public final ArrayList<String> targetSelection(UUID uuid) {
        Set<String> set = new HashSet<>();
        set.addAll(getOwnedDoll(uuid));
        set.addAll(getSharedDoll(uuid));
        set.retainAll(getOnlineDoll());
        return new ArrayList<>(){{addAll(set);}};
    }
    @Override
    public List<Object> tabSuggestion() {
        return List.of(List.of("[true]","[false]"));
    }
}