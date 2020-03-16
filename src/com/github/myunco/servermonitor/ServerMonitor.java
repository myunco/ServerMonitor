package com.github.myunco.servermonitor;

import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import com.github.myunco.servermonitor.listener.PluginEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
/*
 * 需求：
 * 1.可记录玩家聊天日志，并可设置为每个玩家单独一个记录文件（默认开启
 * 2.可记录玩家指令日志，并且日志中标明是否有权限使用 后续同上
 * 3.可记录玩家游戏模式变更日志，后续同上（通常不推荐，默认不开启
 * 4.可记录op变更记录，格式：何时：谁 使 谁 成为/失去op
 * 5.监测非op且不在插件白名单内的玩家 有权 使用特殊关照列表内的指令
 *   5.1上述判断成立后，有以下可选的不冲突的处理方式：
 *     5.1.0什么也不做
 *     5.1.1发送全服公告
 *     5.1.2控制台执行指令
 *     5.1.3使该玩家执行指令
 *     5.1.4使该玩家发送消息
 *     5.1.5对该玩家发送消息
 *     5.1.6控制台显示警告信息
 *     5.1.7将警告信息保存至警告日志
 *   5.2以上内容可在配置文件内进行个性化配置
 * 6.可记录玩家加入/离开/被踢出服务器
 * 7.支持语言文件，默认提供zh_cn简体中文
 * 8.尽力优化插件代码，避免资源浪费
 * 简要分析：
 * 1.监听玩家聊天事件
 * 2.监听玩家指令事件
 * 3.监听游戏模式变更事件（如果有
 * 4.在监听玩家指令事件中处理
 * 5.在监听玩家指令事件中处理
 * 6.监听玩家进入/离开/被踢出事件
 * 7.这个还没想好怎么搞
 * 8.尽力而为
 */
public class ServerMonitor extends JavaPlugin {
    public static ServerMonitor plugin;

    @Override
    public void onEnable() {
        plugin = this;
        ConfigLoader.load();
        if (ConfigLoader.error) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(), this);
        Bukkit.getPluginCommand("ServerMonitor").setExecutor(new PluginCommandExecutor());
        Bukkit.getConsoleSender().sendMessage( "§3[§aServerMonitor§3] §b已启用.");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage( "§3[§aServerMonitor§3] §c已卸载.");
    }

}
