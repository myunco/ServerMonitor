package com.github.myunco.servermonitor.listener;

import com.github.myunco.servermonitor.config.Config;
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

public class PluginEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
        String str = Util.getTime() + " 玩家[" + event.getPlayer().getName() + "]说 : " + event.getMessage();
        Log.writeChatLog(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        String playerName = event.getPlayer().getName();
        boolean isOp = event.getPlayer().isOp();
        String str = Util.getTime() + " 玩家[" + playerName + "]" + (isOp ? "(OP)" : "(非OP)") + "执行命令 : " + cmd;
        Log.writeCommandLog(str);
        //不知道怎么判断非op玩家是否有权限执行这条命令，干脆改成只检测op执行吧
        if (!isOp)
            return;
        if (cmd.toLowerCase().startsWith("/op ")) {
            str = Util.getTime() + " 玩家[" + playerName + "]Opped : " + Util.getTextRight(cmd, " ");
            Log.writeOpChangeLog(str);
        } else if (cmd.toLowerCase().startsWith("/deop ")) {
            str = Util.getTime() + " 玩家[" + playerName + "]De-Opped : " + Util.getTextRight(cmd, " ");
            Log.writeOpChangeLog(str);
        }
        if (Util.isWhiteList(playerName))
            return;
        if (Util.isCommandWhiteList(Util.getTextLeft(cmd, " ")))
            return;
        //str = Util.getTime() + "玩家[" + playerName + "]是OP且不在白名单内并使用了非白名单命令：" + cmd;
        //Bukkit.broadcastMessage(str);
        if (Config.cancel)
            event.setCancelled(true);
        int method = Config.handleMethod;
        if (method == 0)
            return;
        List<String> list;
        if ((method & 1) == 1) {
            list = Config.handleMethodConfig.get("broadcast");
            //list.forEach(Bukkit::broadcastMessage);
            list.forEach(value -> Bukkit.broadcastMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 2) == 2) {
            list = Config.handleMethodConfig.get("consoleCmd");
            list.forEach(value -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.replace("{player}", playerName).replace("{command}", cmd)));
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
            list.forEach(value -> Bukkit.getConsoleSender().sendMessage(value.replace("{player}", playerName).replace("{command}", cmd)));
        }
        if ((method & 64) == 64) {
            list = Config.handleMethodConfig.get("warningLog");
            try {
                Log.createWarningLog();
                list.forEach(Log::writeWarningLog);
                try {
                    Log.closeWarningLog();
                } catch (IOException e) {
                    Log.sendException("§4[错误] §5在关闭WarningLog时发生IO异常!", e.getMessage());
                }
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开WarningLog时发生IO异常!", e.getMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void serverCommandEvent(ServerCommandEvent event) {
        String cmd = event.getCommand();
        String str = Util.getTime() + " 控制台[" + event.getSender().getName() + "]执行命令 : " + cmd;
        Log.writeCommandLog(str);
        if (cmd.toLowerCase().startsWith("/op ")) {
            str = Util.getTime() + " 控制台[" + event.getSender().getName() + "]Opped : " + Util.getTextRight(cmd, " ");
            Log.writeOpChangeLog(str);
        } else if (cmd.toLowerCase().startsWith("/deop ")) {
            str = Util.getTime() + " 控制台[" + event.getSender().getName() + "]De-Opped : " + Util.getTextRight(cmd, " ");
            Log.writeOpChangeLog(str);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        String str = Util.getTime() + " 玩家[" + event.getPlayer().getName() + "]的游戏模式更改为 : " + event.getNewGameMode().toString();
        Log.writeGameModeLog(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoinEvent(PlayerJoinEvent event) {
        String str = Util.getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 加入服务器";
        Log.writeJoinLeaveLog(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuitEvent(PlayerQuitEvent event) {
        String str = Util.getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 退出服务器";
        Log.writeJoinLeaveLog(str);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerKickEvent(PlayerKickEvent event) {
        if (event.isCancelled())
            return;
        String str = Util.getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 被踢出游戏";
        Log.writeJoinLeaveLog(str);
    }

}
