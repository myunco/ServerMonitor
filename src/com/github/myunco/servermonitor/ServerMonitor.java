package com.github.myunco.servermonitor;

import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.config.Language;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import com.github.myunco.servermonitor.listener.PluginEventListener;
import com.github.myunco.servermonitor.util.Log;
import com.github.myunco.servermonitor.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
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
 */
public class ServerMonitor extends JavaPlugin {
    public static ServerMonitor plugin;
    public static ConsoleCommandSender consoleSender;
    BukkitScheduler bukkitScheduler = Bukkit.getScheduler();

    @Override
    public void onEnable() {
        plugin = this;
        consoleSender = Bukkit.getConsoleSender();
        if (!ConfigLoader.load()) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(), this);
        Objects.requireNonNull(getServer().getPluginCommand("ServerMonitor")).setExecutor(new PluginCommandExecutor());
        consoleSender.sendMessage(Language.enabled);
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
        Bukkit.getScheduler().cancelTasks(this);
        Log.closeAllLog();
    }

}
