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
import java.util.HashMap;
import java.util.List;

public class PluginEventListener implements Listener {
    SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormat);

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "]说 : " + event.getMessage();
        Bukkit.broadcastMessage(str);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        String playerName = event.getPlayer().getName();
        boolean isOp = event.getPlayer().isOp();
        String str = getTime() + " 玩家[" + playerName + "]" + (isOp ? "(OP)" : "(非OP)") + "执行指令 : " + cmd;
        Bukkit.broadcastMessage(str);
        //不知道怎么判断非op玩家是否有权限执行这条指令，假装我没想到非op玩家有权限使用/op等指令吧
        if (!isOp)
            return;
        if (cmd.toLowerCase().startsWith("/op ")) {
            str = getTime() + " 玩家[" + playerName + "]Opped : " + getTextRight(cmd, " ");
            Bukkit.broadcastMessage(str);
        } else if (cmd.toLowerCase().startsWith("/deop ")) {
            str = getTime() + " 玩家[" + playerName + "]De-Opped : " + getTextRight(cmd, " ");
            Bukkit.broadcastMessage(str);
        }
        if (Config.whitelist.contains(playerName))
            return;
        if (Config.alertCommandList.contains(getTextLeft(cmd, " "))) {
            str = getTime() + "玩家[" + playerName + "]是OP且不在白名单内并使用了特殊关照命令：" + cmd;
            Bukkit.broadcastMessage(str);
            int method = Config.handleMethod;
            if (method == 0)
                return;
            HashMap<String, List<String>> handleMethodConfig = Config.handleMethodConfig;
            List<String> list;
            if ((method & 1) == 1) {
                list = handleMethodConfig.get("broadcast");
                list.forEach(Bukkit::broadcastMessage);
            }
            if ((method & 2) == 2) {
                list = handleMethodConfig.get("consoleCmd");
                list.forEach(value -> Bukkit.dispatchCommand(ServerMonitor.plugin.getServer().getConsoleSender(), value));
            }
            if ((method & 4) == 4) {
                System.out.println("选项包含4");
            }
            if ((method & 8) == 8) {
                System.out.println("选项包含8");
            }
            if ((method & 16) == 16) {
                System.out.println("选项包含16");
            }
            if ((method & 32) == 32) {
                System.out.println("选项包含32");
            }
            if ((method & 64) == 64) {
                System.out.println("选项包含64");
            }
        }
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerKickEvent(PlayerKickEvent event) {
        if (event.isCancelled())
            return;
        String str = getTime() + " 玩家[" + event.getPlayer().getName() + "](" + event.getPlayer().getAddress().toString() + ") : 被踢出游戏";
        Bukkit.broadcastMessage(str);
    }

    public String getTime() {
        Date d = new Date();
        return sdf.format(d);
    }

    public String getTextRight(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(index + subStr.length());
    }

    public String getTextLeft(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(0, index);
    }
}
