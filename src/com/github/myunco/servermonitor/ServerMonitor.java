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

/*
 * 需求：
 * 1.可记录玩家聊天日志，并可设置为每个玩家单独一个记录文件（默认开启
 * 2.可记录玩家命令日志，并且日志中标明是否为OP，并可设置为每个玩家单独一个记录文件（默认开启
 *   2.1包括控制台执行的命令
 * 3.可记录玩家游戏模式变更日志，并可设置为每个玩家单独一个记录文件（通常不推荐，默认不开启
 * 4.可记录op变更记录，格式----何时：谁 使 谁 成为/失去op
 * 5.可监测不在插件白名单内的OP玩家使用不在命令白名单内的命令
 *   5.1可设置取消该命令执行并有以下可选的不冲突的处理方式：
 *     5.1.0什么也不做
 *     5.1.1发送全服公告
 *     5.1.2控制台执行命令
 *     5.1.3使该玩家执行命令
 *     5.1.4使该玩家发送消息
 *     5.1.5对该玩家发送消息
 *     5.1.6控制台显示警告信息
 *     5.1.7将警告信息保存至警告日志
 *   5.2以上内容可在配置文件内进行个性化配置
 * 6.可记录玩家加入/退出服务器以及被踢出游戏
 * 7.支持语言文件，默认提供zh_cn简体中文
 * 8.尽力优化插件代码，避免资源浪费
 * 简要分析：
 * 监听玩家聊天事件
 * 监听玩家命令事件
 *   在监听玩家命令事件中检测OP变更命令以及判断是否非白名单op执行非白名单命令
 * 监听控制台命令事件
 *   在监听控制台命令事件中检测OP变更命令
 * 监听游戏模式变更事件
 * 监听玩家加入/退出/被踢出事件
 *   如果为每个玩家单独一个记录文件则在玩家加入/退出事件中创建/关闭流
 *     用HashMap为每个玩家存储
 * 语言文件
 *   默认提供zh_cn
 *   语言文件有版本号
 *     如果读取的语言文件版本低于最新版本，进行更新处理
 *   如果指定的语言文件不存在，则把zh_cn复制为指定的语言文件
 * 2020/3/19 17:45
 * 好像没有什么明显的bug...
 * 算是基本完成了!
 *
 * 1.1.0 版本需求
 * 日志改为每天一个文件(OP修改日志和警告日志除外)
 * 添加自动压缩旧日志的选项
 * 添加自动删除多少天前的日志的选项
 *
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
        getServer().getPluginCommand("ServerMonitor").setExecutor(new PluginCommandExecutor());
        //enable(); ConfigLoader.load()的时候会调用
        //Bukkit.getConsoleSender().sendMessage("§3[§aServerMonitor§3] §b已启用.");
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
        //Bukkit.getConsoleSender().sendMessage("§3[§aServerMonitor§3] §c已卸载.");
        consoleSender.sendMessage(Language.disabled);
    }

    public void disable() {
        Bukkit.getScheduler().cancelTasks(this);
        Log.closeAllLog();
    }

}
