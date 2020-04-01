package com.github.myunco.servermonitor;

import com.github.myunco.servermonitor.config.ConfigLoader;
import com.github.myunco.servermonitor.config.Language;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import com.github.myunco.servermonitor.listener.PluginEventListener;
import com.github.myunco.servermonitor.util.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
 * 更新日志：
 * 1.0.1 处理了可能发生的空指针异常
 * 1.0.2 修改处理空指针异常的代码
 * 1.0.3 增加了不记录命令方块执行命令的选项
 *       (修改了config.yml配置文件的读取)
 */
public class ServerMonitor extends JavaPlugin {
    public static ServerMonitor plugin;

    @Override
    public void onEnable() {
        plugin = this;
        if (!ConfigLoader.load()) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(), this);
        Bukkit.getPluginCommand("ServerMonitor").setExecutor(new PluginCommandExecutor());
        //Bukkit.getConsoleSender().sendMessage("§3[§aServerMonitor§3] §b已启用.");
        Bukkit.getConsoleSender().sendMessage(Language.enabled);
    }

    @Override
    public void onDisable() {
        Log.closeAllLog();
        //Bukkit.getConsoleSender().sendMessage("§3[§aServerMonitor§3] §c已卸载.");
        Bukkit.getConsoleSender().sendMessage(Language.disabled);
    }

}
