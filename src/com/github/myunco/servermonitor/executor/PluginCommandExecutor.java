package com.github.myunco.servermonitor.executor;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommandExecutor implements CommandExecutor {
    /*
    public static final String HELP_MSG =
            "§e===========§bServerMonitor§e===========\n" +
            "/ServerMonitor help ---- §a查看指令帮助\n" +
            "§e/ServerMonitor reload ---- §a重载插件配置\n" +
            "§e/ServerMonitor version ---- §a查看插件版本";
    public static final String MSG_PREFIX = "§3[§aServerMonitor§3] §e-> ";
    */
    public static final String VERSION = ServerMonitor.plugin.getDescription().getVersion();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ("ServerMonitor".equals(cmd.getName())) {
            if (args.length == 0)
                return false;
            switch (args[0].toLowerCase()) {
                case "reload":
                    ConfigLoader.reload();
                    sender.sendMessage(Language.MSG_PREFIX + Language.reloaded);
                    break;
                case "help":
                    sender.sendMessage(Language.helpMsg);
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
}
