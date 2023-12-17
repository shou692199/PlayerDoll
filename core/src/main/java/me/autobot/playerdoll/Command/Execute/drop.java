package me.autobot.playerdoll.Command.Execute;

import me.autobot.playerdoll.CarpetMod.EntityPlayerActionPack;
import me.autobot.playerdoll.Dolls.IDoll;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Command.Helper.DollDataValidator;
import me.autobot.playerdoll.Command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class drop extends SubCommand {
    Player player;
    IDoll doll;
    String actionslot = "-1";
    boolean all = false;
    int interval = 0;
    int offset = 0;
    public drop() {}
    public drop(CommandSender sender, Object doll, Object args) {
        setPermission(MinPermission.Share, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName, "Drop")) return;
        player = (Player) sender;
        DollDataValidator validator = new DollDataValidator(player, dollName, dollName.substring(1));
        if (validator.isDollOffline()) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        if (args != null) {
            String[] _args = (String[]) args;
            try {
                this.actionslot = _args[0].toLowerCase();
                this.all = _args[1].equalsIgnoreCase("stack");
                this.interval = Integer.parseInt(_args[2]);
                this.offset = Integer.parseInt(_args[3]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            }
        }
        execute();
    }

    @Override
    public void execute() {
        //EntityPlayerActionPack actionPack = doll.getActionPack();
        EntityPlayerActionPack.Action action = EntityPlayerActionPack.Action.once();
        int slot = -1;
        boolean start = false;

        switch (actionslot) {
            case "once" -> start = true;
            case "continuous" -> {
                start = true;
                action = EntityPlayerActionPack.Action.continuous();
            }
            case "interval" -> {
                start = true;
                action = EntityPlayerActionPack.Action.interval(interval,offset);
            }
            case "helmet" -> slot = 39;
            case "chestplate" -> slot = 38;
            case "leggings" -> slot = 37;
            case "boots" -> slot = 36;
            case "offhand" -> slot = 40;
            case "everything" -> slot = -2;
            default -> {
                try {
                    int num = Integer.parseInt(actionslot);
                    if (num < 1 || num > 36) break;
                    slot = num - 1;
                } catch (NumberFormatException ignored) {
                    //player.sendMessage(LangFormatter.YAMLReplaceMessage("WrongArgument",'&'));
                }
            }
        };
        if (start) {
            doll.getActionPack().start(all ? EntityPlayerActionPack.ActionType.DROP_STACK : EntityPlayerActionPack.ActionType.DROP_ITEM, action);
        } else {
            doll.getActionPack().drop(slot, all);
        }
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
        return List.of(
                List.of("{1..36}","helmet","chestplate","leggings","boots","offhand","everything","once","interval","continuous"),
                List.of("stack"),
                List.of("<[tick:interval]>"),
                List.of("<[tick:offset]>")
        );
    }
}