package com.github.myunco.servermonitor.executor;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.ConfigLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginCommandExecutor implements CommandExecutor {
    public static final String HELP_MSG =
            "§e===========§bServerMonitor§e===========\n" +
            "/ServerMonitor help ---- §a查看指令帮助\n" +
            "§e/ServerMonitor reload ---- §a重载插件配置\n" +
            "§e/ServerMonitor version ---- §a查看插件版本";
    public static final String MSG_PREFIX = "§3[§aServerMonitor§3] §e-> ";
    public static final String VERSION = ServerMonitor.plugin.getDescription().getVersion();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ("ServerMonitor".equals(cmd.getName())) {
            if (args.length == 0)
                return false;
            switch (args[0].toLowerCase()) {
                case "reload":
                    ConfigLoader.reload();
                    sender.sendMessage(MSG_PREFIX + "§a插件配置重载完成.");
                    break;
                case "help":
                    sender.sendMessage(HELP_MSG);
                    break;
                case "version":
                    sender.sendMessage(MSG_PREFIX + "§bVersion§e: §a" + VERSION);
                    break;
                default:
                    sender.sendMessage(MSG_PREFIX + "§c错误的命令参数!\n" + HELP_MSG);
            }
        }
        return true;
    }
}
