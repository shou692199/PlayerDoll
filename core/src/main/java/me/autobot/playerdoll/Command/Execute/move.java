package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class move extends SubCommand {
    Player player;
    IDoll doll;
    String[] args;
    public move() {}
    public move(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Move")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        this.args = args == null? new String[]{""} : (String[]) args;
        execute();
    }

    @Override
    public void execute() {
        float v = 0.0f;
        if (args[0].equalsIgnoreCase("forward")) {
            v = 1.0f;
        } else if (args[0].equalsIgnoreCase("backward")) {
            v = -1.0f;
        }
        doll.getActionPack().setForward(v);
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
        return List.of(List.of("forward","backward"));
    }
}