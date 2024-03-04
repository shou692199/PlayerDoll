package me.autobot.playerdoll.Command.Utils;

import me.autobot.playerdoll.Dolls.DollConfigHelper;
import me.autobot.playerdoll.Dolls.DollManager;
import me.autobot.playerdoll.PlayerDoll;
import me.autobot.playerdoll.Util.LangFormatter;
import me.autobot.playerdoll.YAMLManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CommandList implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            File dollDirectory = new File(PlayerDoll.getDollDirectory());
            //FilenameFilter filter = (dir, name) -> name.endsWith(".yml");
            int page = 0;
            args = args == null || args.length == 0 ? new String[]{"1"} : args;
            File[] dollDirectories = dollDirectory.listFiles();
            if (dollDirectories == null || dollDirectories.length == 0) {
                return true;
            }
            //var filterlist = dollDirectory.list(filter);
            //if (filterlist == null || filterlist.length == 0) return true;
            //int max = (int) Math.ceil(filterlist.length/10.0);
            int max = (int) Math.ceil(dollDirectories.length/10.0);
            try {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0 || page >= max) page = 0;
            } catch (NumberFormatException ignored) {
            }

            for (int i = 10 * page; i < Math.min(10+10*page , dollDirectories.length); i++) {
                var color = ChatColor.GREEN;
                File dollFile = dollDirectories[i];
                YamlConfiguration dollConfig = DollConfigHelper.getConfig(dollFile);
                UUID dollUUID = UUID.fromString(dollConfig.getString("UUID"));
                String name = dollFile.getName().substring(0, dollFile.getName().length()-4);
                //String name = filterlist[i].substring(0,filterlist[i].length()-4);
                if (!DollManager.ONLINE_DOLL_MAP.containsKey(dollUUID)) {
                    color = ChatColor.GRAY;
                }
                //if (!PlayerDoll.dollManagerMap.containsKey(name)) color = ChatColor.GRAY;
                //var yaml = ConfigManager.configs.get(ConfigType.CONFIG);
                var config = YAMLManager.loadConfig(name,false, true);
                var yaml = config.getConfig();
                //var removed = yaml.getBoolean("Remove")? ChatColor.STRIKETHROUGH : "";
                TextComponent hoverText = new TextComponent( i+1 + ". " + color + /*removed*/ name.substring(1));
                hoverText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(LangFormatter.YAMLReplace("commandList.creationDate",yaml.getString("Timestamp"))+"\n")
                                .append(LangFormatter.YAMLReplace("commandList.owner",yaml.getString("Owner.Name"))+"\n")
                                .append(LangFormatter.YAMLReplace("commandList.skin",yaml.getString("SkinData.Name"))+"\n")
                                .create()));
                config.unloadConfig();
                //hoverText.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/doll ? "+ index.get(i)));
                p.spigot().sendMessage(hoverText);
            }
            p.sendMessage(LangFormatter.YAMLReplace("commandPage.footer",page+1,max));
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return List.of("<page>");
    }
}