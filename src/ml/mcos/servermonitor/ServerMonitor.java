package ml.mcos.servermonitor;

import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.ConfigLoader;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.command.CommandServerMonitor;
import ml.mcos.servermonitor.listener.PluginEventListener;
import ml.mcos.servermonitor.metrics.Metrics;
import ml.mcos.servermonitor.util.Log;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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
 *       (指定api-version为1.13)
 *       其他细节优化.
 * 1.2.0 接入bStats.org匿名统计信息
 *       优化op修改判断逻辑、修复自动设置白名单功能可能出现的错误.
 *       调整部分事件优先级、部分已取消事件不再记录日志.
 *       兼容1.7.2以及之前的版本.
 *       配置文件、语言文件、日志文件统一使用UTF8编码读写，不再使用系统默认编码.
 *       自动压缩旧日志选项现在默认关闭.
 *       禁用即时保存时，现在每半小时额外保存一次.
 *       修改所有日志文件的创建时机，现在当真正需要写出日志时才创建.
 *       (修改配置文件加载逻辑)
 *       其他细节优化.
 */
public class ServerMonitor extends JavaPlugin {
    public static ServerMonitor plugin;
    public static int mcVersion = getMinecraftVersion();
    public static int mcVersionPatch;
    public static ConsoleCommandSender consoleSender; // = Bukkit.getConsoleSender();  这样写在1.7.2下面用到consoleSender会NPE···
    public static BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
    static Method getOnlinePlayers;

    @Override
    public void onEnable() {
        plugin = this;
        consoleSender = getServer().getConsoleSender();
        getLogger().info("Minecraft version = 1." + mcVersion + (mcVersionPatch != 0 ? "." + mcVersionPatch : ""));
        ConfigLoader.load();
        //noinspection ConstantConditions
        getServer().getPluginCommand("ServerMonitor").setExecutor(new CommandServerMonitor());
        getServer().getPluginManager().registerEvents(new PluginEventListener(mcVersion), this);
        new Metrics(this, 12934);
        consoleSender.sendMessage(Language.enabled);
    }

    public void enable() {
        Util.processOldLog();
        bukkitScheduler.runTaskTimerAsynchronously(this, () -> {
            if (!Config.realTimeSave) {
                Log.flushAllLog();
            }
            String logName = Util.getToday();
            if (!Util.logName.equals(logName)) {
                Log.updateLog(logName);
                Util.processOldLog();
            }
        }, 36000, 36000); //30*60*20=36000 半小时检查一次
        if (Config.checkUpdate) {
            bukkitScheduler.runTaskTimerAsynchronously(this, () -> {
                try {
                    Util.checkVersionUpdate();
                } catch (IOException e) {
                    Util.sendException(Language.messageCheckUpdateException, e.getMessage());
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

    public static int getMinecraftVersion() {
        String[] version = Bukkit.getBukkitVersion().replace('-', '.').split("\\.");
        int minor = Integer.parseInt(version[1]);
        try {
            mcVersionPatch = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {
        }
        return minor;
    }

    public Collection<? extends Player> getOnlinePlayers() {
        if (mcVersion > 7 || (mcVersion == 7 && mcVersionPatch == 10)) {
            return getServer().getOnlinePlayers();
        }
        try {
            if (getOnlinePlayers == null) {
                getOnlinePlayers = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            }
            return Arrays.asList((Player[]) getOnlinePlayers.invoke(getServer()));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
