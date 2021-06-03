package com.github.myunco.servermonitor.listener;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.Language;
import com.github.myunco.servermonitor.util.Log;
import com.github.myunco.servermonitor.util.Util;
import org.bukkit.Bukkit;
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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PluginEventListener implements Listener {

    //emm 吃了没看文档的亏，没想到名字叫最低的居然最先执行(先入为主了优先级高=先执行 的概念)，这命名好迷惑。
    //@EventHandler(priority = EventPriority.MONITOR)
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
        if (!Config.playerChat.get("enable"))
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerChat
                .replace("{player}", playerName)
                .replace("{message}", event.getMessage());
        Log.writeChatLog(str);
        if (Config.playerChat.get("perPlayer"))
            Log.writePlayerChatLog(playerName, str);
    }

    //@EventHandler(priority = EventPriority.MONITOR)
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (!Config.playerCommand.get("enable"))
            return;
        String cmd = event.getMessage();
        String playerName = event.getPlayer().getName();
        boolean isOp = event.getPlayer().isOp();
        String str = Util.getTime() + Language.logPlayerCommand
                .replace("{player}", playerName)
                .replace("{op?}", isOp ? Language.logIsOp : Language.logNonOp)
                .replace("{command}", cmd);
        Log.writeCommandLog(str);
        if (Config.playerCommand.get("perPlayer"))
            Log.writePlayerCommandLog(playerName, str);
        //待办：寻找新的可靠的监视op修改的方法
        if (isOp && Config.opChange) {
            if (cmd.toLowerCase().startsWith("/op ")) {
                String arg = Util.getTextRight(cmd, " ");
                if (Util.checkOPChangeArg(arg)) {
                    str = Util.getTime() + Language.logOpped
                            .replace("{player1}", playerName)
                            .replace("{player2}", arg.trim());
                    Log.writeOpChangeLog(str);
                }
            } else if (cmd.toLowerCase().startsWith("/deop ")) {
                String arg = Util.getTextRight(cmd, " ");
                if (Util.checkOPChangeArg(arg)) {
                    str = Util.getTime() + Language.logDeOpped
                            .replace("{player1}", playerName)
                            .replace("{player2}", arg.trim());
                    Log.writeOpChangeLog(str);
                }
            }
        }
        if (!Config.commandAlert || Util.isWhiteList(playerName) || Util.isCommandWhiteList(Util.getTextLeft(cmd, " ")))
            return;
        if (Config.cancel)
            event.setCancelled(true);
        int method = Config.handleMethod;
        if (method == 0)
            return;
        List<String> list;
        if ((method & 1) == 1) {
            list = Config.handleMethodConfig.get("broadcast");
            list.forEach(value -> Bukkit.broadcastMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 2) == 2) {
            list = Config.handleMethodConfig.get("consoleCmd");
            list.forEach(value -> Bukkit.dispatchCommand(ServerMonitor.consoleSender, value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 4) == 4) {
            list = Config.handleMethodConfig.get("playerCmd");
            //performCommand要去掉 '/' 所以这里直接用chat吧
            list.forEach(value -> event.getPlayer().chat(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 8) == 8) {
            list = Config.handleMethodConfig.get("playerSendMsg");
            list.forEach(value -> event.getPlayer().chat(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 16) == 16) {
            list = Config.handleMethodConfig.get("sendMsgToPlayer");
            list.forEach(value -> event.getPlayer().sendMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 32) == 32) {
            list = Config.handleMethodConfig.get("consoleWarning");
            list.forEach(value -> ServerMonitor.consoleSender.sendMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 64) == 64) {
            list = Config.handleMethodConfig.get("warningLog");
            try {
                Log.createWarningLog();
                list.forEach(value -> Log.writeWarningLog(Util.getTime() + value.replace("{player}", playerName).replace("{command}", cmd)));
                try {
                    Log.closeWarningLog();
                } catch (IOException e) {
                    Log.sendException(Language.messageCloseException.replace("{file}", "Warning.log"), e.getMessage());
                }
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "Warning.log"), e.getMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void serverCommandEvent(ServerCommandEvent event) {
        if (!Config.playerCommand.get("consoleCommand"))
            return;
        String name = event.getSender().getName();
        if (!Config.playerCommand.get("commandBlockCommand") && "@".equals(name))
            return;
        String cmd = event.getCommand();
        String str = Util.getTime() + Language.logConsoleCommand
                .replace("{sender}", name)
                .replace("{command}", cmd);
        Log.writeCommandLog(str);
        if (!Config.opChange)
            return;
        if (cmd.toLowerCase().startsWith("op ")) {
            String playerName = Util.getTextRight(cmd, " ");
            if (Util.checkOPChangeArg(playerName)) {
                playerName = playerName.trim();
                str = Util.getTime() + Language.logConsoleOpped
                        .replace("{sender}", name)
                        .replace("{player}", playerName);
                if ("CONSOLE".equals(name)) {
                    Util.addWhiteList(playerName);
                }
                Log.writeOpChangeLog(str);
            }
        } else if (cmd.toLowerCase().startsWith("deop ")) {
            String playerName = Util.getTextRight(cmd, " ");
            if (Util.checkOPChangeArg(playerName)) {
                playerName = playerName.trim();
                str = Util.getTime() + Language.logConsoleDeOpped
                        .replace("{sender}", name)
                        .replace("{player}", playerName);
                if ("CONSOLE".equals(name)) {
                    Util.delWhiteList(playerName);
                }
                Log.writeOpChangeLog(str);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        if (!Config.playerGameModeChange.get("enable"))
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerGameModeChange
                .replace("{player}", playerName)
                .replace("{gamemode}", event.getNewGameMode().toString());
        Log.writeGameModeLog(str);
        if (Config.playerGameModeChange.get("perPlayer"))
            Log.writePlayerGameModeLog(playerName, str);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoinEvent(PlayerJoinEvent event) {
        if (!Config.joinAndLeave)
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() + Language.logPlayerJoin
                .replace("{player}", playerName)
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString());
        Log.writeJoinLeaveLog(str);
        if (Config.playerChat.get("perPlayer"))
            Log.addPlayerChatLog(playerName);
        if (Config.playerCommand.get("perPlayer"))
            Log.addPlayerCommandLog(playerName);
        if (Config.playerGameModeChange.get("perPlayer"))
            Log.addPlayerGameModeLog(playerName);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerQuitEvent(PlayerQuitEvent event) {
        if (!Config.joinAndLeave)
            return;
        String playerName = event.getPlayer().getName();
        String str = Util.getTime() +Language.logPlayerQuit
                .replace("{player}", playerName)
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString());
        Log.writeJoinLeaveLog(str);
        if (Config.playerChat.get("perPlayer"))
            Log.closePlayerChatLog(playerName);
        if (Config.playerCommand.get("perPlayer"))
            Log.closePlayerCommandLog(playerName);
        if (Config.playerGameModeChange.get("perPlayer"))
            Log.closePlayerGameModeLog(playerName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerKickEvent(PlayerKickEvent event) {
        if (!Config.joinAndLeave || event.isCancelled())
            return;
        String str = Util.getTime() + Language.logPlayerKick
                .replace("{player}", event.getPlayer().getName())
                .replace("{ip}", Objects.requireNonNull(event.getPlayer().getAddress()).toString())
                .replace("{reason}", event.getReason());
        Log.writeJoinLeaveLog(str);
    }

}
