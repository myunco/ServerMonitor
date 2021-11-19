package ml.mcos.servermonitor.command;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.ConfigLoader;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandServerMonitor implements TabExecutor {
    public static final String VERSION = ServerMonitor.plugin.getDescription().getVersion();
    public static List<String> tabList = Arrays.asList("help", "reload", "version");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
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
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Util.getCompleteList(args, tabList);
        }
        return Collections.emptyList();
    }
}
