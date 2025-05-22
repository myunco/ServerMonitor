package net.myunco.servermonitor;

import net.myunco.folia.FoliaCompatibleAPI;
import net.myunco.folia.scheduler.CompatibleScheduler;
import net.myunco.servermonitor.command.CommandServerMonitor;
import net.myunco.servermonitor.config.Config;
import net.myunco.servermonitor.config.Language;
import net.myunco.servermonitor.listener.PluginEventListener;
import net.myunco.servermonitor.metrics.Metrics;
import net.myunco.servermonitor.update.UpdateChecker;
import net.myunco.servermonitor.update.UpdateNotification;
import net.myunco.servermonitor.util.Log;
import net.myunco.servermonitor.util.Util;
import net.myunco.servermonitor.util.Version;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServerMonitor extends JavaPlugin {
    private static ServerMonitor plugin;
    private static Timer timer;
    public Version mcVersion;
    public CompatibleScheduler scheduler;
    private ConsoleCommandSender console;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = new Version(getServer().getBukkitVersion());
        console = getServer().getConsoleSender();
        initFoliaCompatibleAPI();
        init();
        PluginCommand command = getCommand("ServerMonitor");
        if (command != null) {
            command.setExecutor(new CommandServerMonitor());
            command.setTabCompleter((TabCompleter) command.getExecutor());
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(this), this);
        if (Config.checkUpdate) {
            getServer().getPluginManager().registerEvents(new UpdateNotification(), this);
        }
        new Metrics(this, 12934);
        logMessage(Language.enableMessage);
    }

    public void init() {
        Config.loadConfig();
        if (Config.checkUpdate) {
            UpdateChecker.start();
        }
        Util.processOldLog();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!Config.realTimeSave) {
                        Log.flushAllLog();
                    }
                    String logName = Util.getToday() + ".log";
                    if (!Util.logName.equals(logName)) {
                        Log.updateLog(logName);
                        Util.processOldLog();
                    }
                }
            }, 30 * 60 * 1000, 30 * 60 * 1000); //半小时检查一次
        }
        Log.init();
    }

    public void initFoliaCompatibleAPI() {
        Plugin api = getServer().getPluginManager().getPlugin("FoliaCompatibleAPI");
        if (api == null) {
            getLogger().warning("FoliaCompatibleAPI not found!");
            File file = new File(getDataFolder().getParentFile(), "FoliaCompatibleAPI-1.2.0.jar");
            InputStream in = getResource("lib/FoliaCompatibleAPI-1.2.0.jar");
            try {
                saveResource(file, in);
                api = getServer().getPluginManager().loadPlugin(file);
                if (api == null) {
                    throw new Exception("FoliaCompatibleAPI load failed!");
                }
                getServer().getPluginManager().enablePlugin(api);
                api.onLoad();
            } catch (Exception e) {
                e.printStackTrace();
                getLogger().severe("未安装 FoliaCompatibleAPI ，本插件无法运行！");
                return;
            }
        }
        scheduler = ((FoliaCompatibleAPI) api).getScheduler(this);
        console.sendMessage("[ServerMonitor] Found FoliaCompatibleAPI: §3v" + api.getDescription().getVersion());
    }

    private void saveResource(File target, InputStream source) throws Exception {
        if (source != null) {
            //noinspection IOStreamConstructor
            OutputStream out = new FileOutputStream(target);
            byte[] buf = new byte[8192];
            int len;
            while ((len = source.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            source.close();
        }
    }

    @Override
    public void onDisable() {
        disable();
        logMessage(Language.disableMessage);
    }

    public void disable() {
        UpdateChecker.stop();
        Log.closeAllLog(true);
    }

    public static ServerMonitor getPlugin() {
        return plugin;
    }

    public ClassLoader classLoader() {
        return getClassLoader();
    }

    public ArrayList<Player> getOnlineOperators() {
        ArrayList<Player> result = new ArrayList<>();
        for (OfflinePlayer operator : getServer().getOperators()) {
            Player player = operator.getPlayer();
            if (player != null) {
                result.add(player);
            }
        }
        return result;
    }

    public void logMessage(String message) {
        console.sendMessage(Language.logPrefix + message);
    }

    public CompatibleScheduler getScheduler() {
        return scheduler;
    }
}
