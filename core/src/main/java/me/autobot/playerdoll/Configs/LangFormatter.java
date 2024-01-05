package me.autobot.playerdoll.Configs;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.util.Arrays;

public class LangFormatter {
    public static String[] splitter(String s) {
        return s.split("\\$n");
    }

    public static String YAMLReplaceMessage(String path, char colorCode) {
        return YAMLFormat("Message." + path, colorCode, null);
    }
    @SafeVarargs
    public static String YAMLReplaceMessage(String path, char colorCode, Pair<CharSequence,CharSequence>... variables) {
        return YAMLFormat("Message." + path, colorCode, variables);
    }

    public static String YAMLReplace(String path, char colorCode) {
        return YAMLFormat(path, colorCode, null);
    }
    @SafeVarargs
    public static String YAMLReplace(String path, char colorCode, Pair<CharSequence,CharSequence>... variables) {
        return YAMLFormat(path, colorCode, variables);
    }


    @SafeVarargs
    private static String YAMLFormat(String path, char colorCode, @Nullable Pair<CharSequence,CharSequence>... variables) {
        var config = ConfigManager.getLanguage();
        if (config == null) return "LANGNOTFOUND";
        String str = config.getString(path);
        if (str == null) return path;
        if (variables != null) {
            for (Pair<CharSequence,CharSequence> pair : variables) {
                CharSequence a = pair.getA() == null ? "A NOTFOUND" : pair.getA();
                CharSequence b = pair.getB() == null ? "B NOTFOUND" : pair.getB();
                str = str.replace(a,b);
            }
        }
        if (path.startsWith("Message")) str = config.getString("MessagePrefix") + " " + str;
        return ChatColor.translateAlternateColorCodes(colorCode, str);
    }
}