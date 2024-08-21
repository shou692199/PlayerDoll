package me.autobot.playerdoll.command.subcommand;

import com.mojang.brigadier.context.CommandContext;
import me.autobot.playerdoll.command.DollCommandExecutor;
import me.autobot.playerdoll.command.SubCommand;
import me.autobot.playerdoll.config.FlagConfig;
import me.autobot.playerdoll.doll.BaseEntity;
import me.autobot.playerdoll.doll.DollManager;
import me.autobot.playerdoll.doll.config.DollConfig;
import me.autobot.playerdoll.util.LangFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Exp extends SubCommand implements DollCommandExecutor {
    private final int level;
    private Player sender;
    public Exp(Player target, int level) {
        super(target);
        this.level = level;
    }

    @Override
    public void execute() {
        getExp(level);
    }

    @Override
    public int onCommand(CommandSender sender, CommandContext<Object> context) {
        if (!(sender instanceof Player playerSender)) {
            sender.sendMessage(LangFormatter.YAMLReplaceMessage("require-player"));
            return 0;
        }
        this.sender = playerSender;
        if (target == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        BaseEntity targetEntity = DollManager.ONLINE_DOLLS.get(target.getUniqueId());
        if (targetEntity == null) {
            playerSender.sendMessage(LangFormatter.YAMLReplaceMessage("no-target"));
            return 0;
        }
        // Direct execute
        if (executeIfManage(context.getInput())) {
            return 1;
        }

        if (!outputHasPerm(playerSender, DollConfig.getOnlineDollConfig(target.getUniqueId()), FlagConfig.PersonalFlagType.EXP)) {
            return 0;
        }

        execute();
        return 1;
    }

    private void getExp(int level) {
        if (target.getLevel() <= 0) {
            return;
        }
        
        if (level == -1) {
            level = target.getLevel();
        }

        int sumPoints = Math.round(target.getExp() * target.getExpToLevel() + sender.getExp() * sender.getExpToLevel());
        target.setExp(0);
        sender.setExp(0);

        for (int i = 0; i < level; ++i) {
            target.setLevel(target.getLevel() - 1);
            int expToLevel = target.getExpToLevel();
            while (expToLevel >= sender.getExpToLevel()) {
                expToLevel -= sender.getExpToLevel();
                sender.setLevel(sender.getLevel() + 1);
            }
            sumPoints += expToLevel;
        }

        while (sumPoints >= sender.getExpToLevel()) {
            sumPoints -= sender.getExpToLevel();
            sender.setLevel(sender.getLevel() + 1);
        }
        if (sumPoints > 0) {
            sender.setExp((float) sumPoints / sender.getExpToLevel());
        }
    }
}
