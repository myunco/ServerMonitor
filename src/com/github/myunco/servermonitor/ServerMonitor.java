package com.github.myunco.servermonitor;

import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.config.Language;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import com.github.myunco.servermonitor.listener.PluginEventListener;
import com.github.myunco.servermonitor.metrics.Metrics;
import com.github.myunco.servermonitor.util.Log;
import com.github.myunco.servermonitor.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/*
 * 更新日志：
 * 1.0.1 处理了可能发生的空指针异常.
 * 1.0.2 修复了NPC触发事件导致的报错.
 * 1.0.3 增加了是否记录命令方块执行命令的选项.
 *       (修改了config.yml配置文件的读取)
 *       (修改了对NPC触发事件的处理方式)
 * 1.1.0 日志文件改为每天一个(opChange和Warning除外).
 *       增加了自动压缩旧日志的选项.
 *       增加了自动删除多少天前的日志的选项.
 *       增加了检查更新选项(默认关闭).
 *       优化命令的TAB补全.
 *       优化代码，修正细节错误.
 *       添加zh_tw语言文件(用户提供).
 * 1.1.1 修复了写警告日志会出现空指针异常的问题.
 * 1.1.2 修复了日期更新后玩家日志没有正确关闭导致的错误.
 *       修复了压缩异常、删除错误消息中{file}无效的错误.
 *       自动检查更新选项改为默认开启.
 *       (修复了插件加载时enable()会调用两次的问题)
 * 1.1.3 增加了在控制台op或deop时自动设置玩家白名单的功能.
 * 1.1.4 命令补全支持大写.
 *       指定api-version为1.13.
 *       其他细节修改.
 * 1.1.5 优化op修改判断逻辑
 *       接入bStats.org匿名统计信息
 *       调整部分事件优先级、部分已取消事件不再记录日志了
 *       现在支持1.7.2以及之前的版本了
 */
public class ServerMonitor extends JavaPlugin {
    public static ServerMonitor plugin;
    public static int mcVersion = Integer.parseInt(Bukkit.getBukkitVersion().replace('-', '.').split("\\.")[1]);
    public static ConsoleCommandSender consoleSender; // = Bukkit.getConsoleSender();  在1.7.2这样写 下面用到consoleSender会NPE···
    public static BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void onEnable() {
        consoleSender = getServer().getConsoleSender();
        getLogger().info("minecraft version = 1." + mcVersion);
        plugin = this;
        if (!ConfigLoader.load()) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(mcVersion), this);
        Objects.requireNonNull(getServer().getPluginCommand("ServerMonitor")).setExecutor(new PluginCommandExecutor());
        consoleSender.sendMessage(Language.enabled);
        new Metrics(this, 12934);
    }

    public void enable() {
        Util.logInit();
        bukkitScheduler.runTaskTimerAsynchronously(this, () -> {
            String logName = Util.getToday();
            if (!Util.logName.equals(logName)) {
                Log.updateAllLog(logName);
                Util.logInit();
            }
        }, 36000, 36000); //30*60*20=36000 半小时检查一次
        if (Config.checkUpdate) {
            bukkitScheduler.runTaskTimerAsynchronously(this, () -> {
                try {
                    Util.checkVersionUpdate();
                } catch (IOException e) {
                    Log.sendException(Language.messageCheckUpdateException, e.getMessage());
                }
            }, 200, 864000); //12*60*60*20=864000 10秒后检查一次，以后每12小时检查一次
        }
    }

    @Override
    public void onDisable() {
        disable();
        consoleSender.sendMessage(Language.disabled);
    }

    public void disable() {
        bukkitScheduler.cancelTasks(this);
        Log.closeAllLog();
    }

    public Player[] getOnlinePlayers() {
        try {
            if (mcVersion > 7) {
                throw new Exception();
            }
            return (Player[]) Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers").invoke(getServer());
        } catch (Exception e) {
            Collection<? extends Player> collection = getServer().getOnlinePlayers();
            Player[] players = new Player[collection.size()];
            collection.toArray(players);
            return players;
        }
    }

}
