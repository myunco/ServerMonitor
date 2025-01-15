package net.myunco.servermonitor.listener;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Config;
import net.myunco.servermonitor.config.Language;
import net.myunco.servermonitor.util.Log;
import net.myunco.servermonitor.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Objects;

public class PluginEventListener implements Listener {
    private final ServerMonitor plugin;
    private final String opGivePermission;
    private final String opTakePermission;

    public PluginEventListener(ServerMonitor plugin) {
        this.plugin = plugin;
        if (plugin.getMcVersion() < 8) {
            opGivePermission = "bukkit.command.op.give";
            opTakePermission = "bukkit.command.op.take";
        } else {
            opGivePermission = "minecraft.command.op";
            opTakePermission = "minecraft.command.deop";
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
        if (!Config.playerChat.get("enable")) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerChat
                .replace("{player}", playerName)
                .replace("{message}", event.getMessage());
        Log.chatLog.write(str);
        if (Config.playerChat.get("perPlayer")) {
            Log.writePlayerChatLog(playerName, str);
        }
        if (Log.chatLog.getDataSource() != null) {
            Log.chatLog.getDataSource().logChat(str, playerName, event.getPlayer().getUniqueId().toString());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (!Config.playerCommand.get("enable")) {
            return;
        }
        String cmd;
        if (Config.hidePassword && isLoginCommand(event.getMessage())) {
            cmd = hidePassword(event.getMessage());
        } else {
            cmd = event.getMessage();
        }
        String playerName = event.getPlayer().getName();
        boolean isOp = event.getPlayer().isOp();
        String str = Util.getTime() + Language.logPlayerCommand
                .replace("{player}", playerName)
                .replace("{op?}", isOp ? Language.logPlayerCommandOp : Language.logPlayerCommandNonOp)
                .replace("{command}", cmd);
        Log.commandLog.write(str);
        if (Config.playerCommand.get("perPlayer")) {
            Log.writePlayerCommandLog(playerName, str);
        }
        if (Log.commandLog.getDataSource() != null) {
            Log.commandLog.getDataSource().logCommand(str, cmd, playerName, event.getPlayer().getUniqueId().toString(), isOp);
        }
        if (Config.keywordsAlertEnable && !isOp && !Config.keywordsAlertMsg.isEmpty()) {
            for (String keyword : Config.keywordsAlertKeywords) {
                if (cmd.toLowerCase().contains(keyword.toLowerCase())) {
                    if (Config.keywordsAlertCancel) {
                        event.setCancelled(true);
                    }
                    if (Config.keywordsAlertReportAdmin) {
                        for (Player player : plugin.getOnlineOperators()) {
                            for (String msg : Config.keywordsAlertMsg) {
                                player.sendMessage(msg.replace("{player}", playerName).replace("{command}", cmd));
                            }
                        }
                    }
                    if (Config.keywordsAlertReportConsole) {
                        for (String msg : Config.keywordsAlertMsg) {
                            Bukkit.getConsoleSender().sendMessage(msg.replace("{player}", playerName).replace("{command}", cmd));
                        }
                    }
                    if (Config.keywordsAlertSaveToLog) {
                        for (String msg : Config.keywordsAlertMsg) {
                            String text = ChatColor.stripColor(msg.replace("{player}", playerName).replace("{command}", cmd));
                            Log.keywordsAlert.write(text);
                            if (Log.keywordsAlert.getDataSource() != null) {
                                Log.keywordsAlert.getDataSource().logKeywordsAlert(text, cmd, playerName);
                            }
                        }
                        Log.keywordsAlert.close();
                    }
                    break;
                }
            }
        }
        if (event.isCancelled()) {
            return;
        }
        if (Config.opChange) {
            if ((isOp || event.getPlayer().hasPermission(opGivePermission)) && cmd.toLowerCase().startsWith("/op ")) {
                String arg = Util.getTextRight(cmd, " ").trim();
                if (arg.indexOf(' ') == -1) {
                    str = Util.getTime() + Language.logOpChangeOpPlayer
                            .replace("{player1}", playerName)
                            .replace("{player2}", arg);
                    Log.opChangeLog.write(str);
                    Log.opChangeLog.close();
                    if (Log.opChangeLog.getDataSource() != null) {
                        Player target = plugin.getServer().getPlayer(arg);
                        Log.opChangeLog.getDataSource().logOpChange(str, playerName, arg, target == null ? "null" : target.getUniqueId().toString(), 1);
                    }
                }
            } else if ((isOp || event.getPlayer().hasPermission(opTakePermission)) && cmd.toLowerCase().startsWith("/deop ")) {
                String arg = Util.getTextRight(cmd, " ").trim();
                if (arg.indexOf(' ') == -1) {
                    str = Util.getTime() + Language.logOpChangeDeopPlayer
                            .replace("{player1}", playerName)
                            .replace("{player2}", arg);
                    Log.opChangeLog.write(str);
                    Log.opChangeLog.close();
                    if (Log.opChangeLog.getDataSource() != null) {
                        Player target = plugin.getServer().getPlayer(arg);
                        Log.opChangeLog.getDataSource().logOpChange(str, playerName, arg, target == null ? "null" : target.getUniqueId().toString(), 0);
                    }
                }
            }
        }
        if (!isOp || !Config.commandAlertEnable || Util.isInWhitelist(playerName) || Util.isWhitelistCommand(Util.getTextLeft(cmd, " "))) {
            return;
        }
        if (Config.commandAlertCancel) {
            event.setCancelled(true);
        }
        int method = Config.commandAlertHandleMethod;
        if (method == 0) {
            return;
        }
        if ((method & 1) == 1) {
            Config.commandAlertHandleMethodConfig.get("broadcast").forEach(value -> Bukkit.broadcastMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 2) == 2) {
            Config.commandAlertHandleMethodConfig.get("consoleCmd").forEach(value -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 4) == 4) {
            //performCommand要去掉 '/' 所以这里直接用chat吧
            Config.commandAlertHandleMethodConfig.get("playerCmd").forEach(value -> event.getPlayer().chat(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 8) == 8) {
            Config.commandAlertHandleMethodConfig.get("playerSendMsg").forEach(value -> event.getPlayer().chat(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 16) == 16) {
            Config.commandAlertHandleMethodConfig.get("sendMsgToPlayer").forEach(value -> event.getPlayer().sendMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 32) == 32) {
            for (int i = 0; i < 10; i++) {
                Config.commandAlertHandleMethodConfig.get("consoleWarning").forEach(value -> Bukkit.getConsoleSender().sendMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
            }
        }
        if ((method & 64) == 64) {
            Config.commandAlertHandleMethodConfig.get("warningLog").forEach(value -> {
                        String text = Util.getTime() + value.replace("{player}", playerName).replace("{command}", cmd);
                        Log.warningLog.write(text);
                        if (Log.warningLog.getDataSource() != null) {
                            Log.warningLog.getDataSource().logWarning(text, playerName);
                        }
                    });
            Log.warningLog.close();
        }
    }

    private final String[] loginCommand = {"/login ", "/log ", "/l ", "/register ", "/reg "};

    private boolean isLoginCommand(String cmd) {
        for (String s : loginCommand) {
            if (cmd.startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private String hidePassword(String cmd) {
        StringBuilder builder = new StringBuilder();
        int index = cmd.indexOf(' ');
        for (int i = 0; i < cmd.length(); i++) {
            if (i <= index) {
                builder.append(cmd.charAt(i));
            } else if (cmd.charAt(i) != ' '){
                builder.append('*');
            } else {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void serverCommandEvent(ServerCommandEvent event) {
        if (!Config.playerCommand.get("consoleCommand")) {
            return;
        }
        String name = event.getSender().getName();
        if (!Config.playerCommand.get("commandBlockCommand") && "@".equals(name)) {
            return;
        }
        String cmd = event.getCommand();
        String str = Util.getTime() + Language.logConsoleCommand
                .replace("{sender}", name)
                .replace("{command}", cmd);
        Log.commandLog.write(str);
        if (Log.commandLog.getDataSource() != null) {
            Log.commandLog.getDataSource().logCommand(str, cmd, name, "none", event.getSender().isOp());
        }
        if (!Config.opChange || plugin.isVersionGtOrEq(8, 7) && event.isCancelled()) { //1.8.7版本开始此事件才实现Cancellable
            return;
        }
        if (!(event.getSender() instanceof ConsoleCommandSender)) { //只有console有权限授予/撤销op
            return;
        }
        if (cmd.toLowerCase().startsWith("op ")) {
            String arg = Util.getTextRight(cmd, " ").trim();
            if (arg.indexOf(' ') == -1) {
                str = Util.getTime() + Language.logOpChangeOpConsole
                        .replace("{sender}", name)
                        .replace("{player}", arg);
                Log.opChangeLog.write(str);
                Log.opChangeLog.close();
                if (Config.commandAlertEnable) {
                    Util.whitelistAdd(arg);
                }
                if (Log.opChangeLog.getDataSource() != null) {
                    Player target = plugin.getServer().getPlayer(arg);
                    Log.opChangeLog.getDataSource().logOpChange(str, name, arg, target == null ? "null" : target.getUniqueId().toString(), 1);
                }
            }
        } else if (cmd.toLowerCase().startsWith("deop ")) {
            String arg = Util.getTextRight(cmd, " ").trim();
            if (arg.indexOf(' ') == -1) {
                str = Util.getTime() + Language.logOpChangeDeopConsole
                        .replace("{sender}", name)
                        .replace("{player}", arg);
                Log.opChangeLog.write(str);
                Log.opChangeLog.close();
                if (Config.commandAlertEnable) {
                    Util.whitelistRemove(arg);
                }
                if (Log.opChangeLog.getDataSource() != null) {
                    Player target = plugin.getServer().getPlayer(arg);
                    Log.opChangeLog.getDataSource().logOpChange(str, name, arg, target == null ? "null" : target.getUniqueId().toString(), 0);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        if (!Config.playerGameModeChange.get("enable")) {
            return;
        }
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerGameModeChange
                .replace("{player}", playerName)
                .replace("{gamemode}", event.getNewGameMode().toString());
        Log.gameModeLog.write(str);
        if (Config.playerGameModeChange.get("perPlayer")) {
            Log.writePlayerGameModeLog(playerName, str);
        }
        if (Log.gameModeLog.getDataSource() != null) {
            Log.gameModeLog.getDataSource().logGameModeChange(str, event.getNewGameMode().toString(), playerName, event.getPlayer().getUniqueId().toString());
        }
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (!Config.joinAndLeave)
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerJoin
                .replace("{player}", playerName)
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString());
        Log.joinLeaveLog.write(str);
        if (Log.joinLeaveLog.getDataSource() != null) {
            Log.joinLeaveLog.getDataSource().logJoinLeave(str, playerName, event.getPlayer().getAddress().toString(), null);
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        if (!Config.joinAndLeave)
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() +Language.logPlayerQuit
                .replace("{player}", playerName)
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString());
        Log.joinLeaveLog.write(str);
        if (Log.joinLeaveLog.getDataSource() != null) {
            Log.joinLeaveLog.getDataSource().logJoinLeave(str, playerName, event.getPlayer().getAddress().toString(), null);
        }
        if (Config.playerChat.get("perPlayer")) {
            Log.closePlayerChatLog(playerName);
        }
        if (Config.playerCommand.get("perPlayer")) {
            Log.closePlayerCommandLog(playerName);
        }
        if (Config.playerGameModeChange.get("perPlayer")) {
            Log.closePlayerGameModeLog(playerName);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerKickEvent(PlayerKickEvent event) {
        if (!Config.joinAndLeave)
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerKick
                .replace("{player}", playerName)
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString())
                .replace("{reason}", event.getReason());
        Log.joinLeaveLog.write(str);
        if (Log.joinLeaveLog.getDataSource() != null) {
            Log.joinLeaveLog.getDataSource().logJoinLeave(str, playerName, event.getPlayer().getAddress().toString(), event.getReason());
        }
    }

}
