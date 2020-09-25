package com.github.myunco.servermonitor.executor;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.config.Language;
import com.github.myunco.servermonitor.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PluginCommandExecutor implements TabExecutor {
    public static final String VERSION = ServerMonitor.plugin.getDescription().getVersion();
    public static List<String> list = Arrays.asList("help", "reload", "version");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if ("ServerMonitor".equals(cmd.getName())) {
            if (args.length == 0)
                return false;
            switch (args[0].toLowerCase()) {
                case "help":
                    sender.sendMessage(Language.helpMsg);
                    break;
                case "reload":
                    ConfigLoader.reload();
                    sender.sendMessage(Language.MSG_PREFIX + Language.reloaded);
                    break;
                case "version":
                    sender.sendMessage(Language.MSG_PREFIX + "§bVersion§e: §a" + VERSION);
                    break;
                default:
                    sender.sendMessage(Language.MSG_PREFIX + Language.commandError + "\n" + Language.helpMsg);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String alias, String[] args) {
        if ("ServerMonitor".equals(cmd.getName())) {
            if (args.length == 1) {
                if (args[0].isEmpty()) {
                    return list;
                }
                return Util.getTabList(args, list, 0);
            }
        }
        return null;
    }
}
