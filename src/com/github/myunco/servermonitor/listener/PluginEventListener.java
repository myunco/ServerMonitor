package com.github.myunco.servermonitor.listener;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
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

import java.text.SimpleDateFormat;
import java.util.Date;

public class PluginEventListener implements Listener {
    SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormat);

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "]说 : " + event.getMessage();
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        PluginCommand pc = ServerMonitor.plugin.getCommand(event.getMessage().substring(1));
        String per = pc == null ? "(未知)" : pc.getPermission();
        if (per == null) {
            per = "(未知)";
        } else if (!per.equals("(未知)")){
            per = pc.testPermissionSilent(event.getPlayer()) ? "(有权限)" : "(无权限)";
        }
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "]" + per + "执行指令 : " + event.getMessage();
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void serverCommandEvent(ServerCommandEvent event) {
        String str = getTime() + " 控制台[" + event.getSender().getName() + "]执行指令 : " + event.getCommand();
        Bukkit.broadcastMessage(str);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "]的游戏模式更改为 : " + event.getNewGameMode().toString();
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoinEvent(PlayerJoinEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 加入服务器";
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuitEvent(PlayerQuitEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 退出服务器";
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerKickEvent(PlayerKickEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 被踢出游戏";
        Bukkit.broadcastMessage(str);
    }

    public String getTime() {
        Date d = new Date();
        return sdf.format(d);
    }
}
