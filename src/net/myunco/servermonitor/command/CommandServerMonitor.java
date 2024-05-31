package net.myunco.servermonitor.command;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

@SuppressWarnings("NullableProblems")
public class CommandServerMonitor implements TabExecutor {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "help":
                sendMessage(sender, Language.commandHelp);
                break;
            case "reload":
                plugin.disable();
                plugin.init();
                sendMessage(sender, Language.commandReload);
                break;
            case "version":
                sendMessage(sender, "§bVersion§e: §a" + plugin.getDescription().getVersion());
                break;
            default:
                sendMessage(sender, Language.commandUnknown);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return TabComplete.getCompleteList(args, TabComplete.getTabList(args, cmd.getName()));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Language.messagePrefix + message);
    }

}
