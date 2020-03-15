package git.myunco.servermonitor.executor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommandExecutor implements CommandExecutor {
    public static final String HELP_MSG =
            "§e===========§bServerMonitor§e===========\n" +
            "/ServerMonitor help ---- §a查看指令帮助\n" +
            "§e/ServerMonitor reload ---- §a重载插件配置\n" +
            "§e/ServerMonitor version ---- §a查看插件版本";
    public static final String MSG_PREFIX = "§3[§aServerMonitor§3] §e-> ";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ("ServerMonitor".equals(cmd.getName())) {
            if (args.length == 0)
                return false;
            switch (args[0].toLowerCase()) {
                case "reload":
                    sender.sendMessage(MSG_PREFIX + "§a你执行了重载指令，然而现在没有重载功能。");
                    break;
                case "help":
                    sender.sendMessage(HELP_MSG);
                    break;
                case "version":
                    sender.sendMessage(MSG_PREFIX + "§bVersion§e: §d1.0.0");
                    break;
                default:
                    sender.sendMessage(MSG_PREFIX + "§c错误的命令参数!\n" + HELP_MSG);
            }
        }
        return true;
    }
}
