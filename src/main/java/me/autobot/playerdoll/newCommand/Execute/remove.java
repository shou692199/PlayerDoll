package me.autobot.playerdoll.newCommand.Execute;

import me.autobot.playerdoll.Dolls.AbstractDoll;
import me.autobot.playerdoll.Dolls.FoliaDollImpl;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.newCommand.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class remove extends SubCommand {
    Player player;
    AbstractDoll doll;

    public remove(CommandSender sender, Object doll, Object args) {
        super(MinPermission.Owner, false);
        String dollName = checkDollName(doll);
        if (!checkPermission(sender, dollName)) return;
        player = (Player) sender;
        if (!isOnline(dollName)) return;
        this.doll = PlayerDoll.dollManagerMap.get(dollName);
        execute();
    }

    @Override
    public void execute() {
        doll.getConfigManager().getData().put("Remove",true);
        if (PlayerDoll.isFolia) ((FoliaDollImpl)doll).foliaDisconnect(true);
        else doll.kill();
    }
}