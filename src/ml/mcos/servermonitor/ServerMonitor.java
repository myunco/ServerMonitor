package ml.mcos.servermonitor;

import ml.mcos.servermonitor.command.CommandServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.listener.PluginEventListener;
import ml.mcos.servermonitor.metrics.Metrics;
import ml.mcos.servermonitor.update.UpdateChecker;
import ml.mcos.servermonitor.util.Log;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;
import java.util.TimerTask;

public class ServerMonitor extends JavaPlugin {
    private static ServerMonitor plugin;
    private static Timer timer;

    @Override
    public void onEnable() {
        plugin = this;
        init();
        PluginCommand command = getCommand("ServerMonitor");
        if (command != null) {
            command.setExecutor(new CommandServerMonitor());
            command.setTabCompleter((TabCompleter) command.getExecutor());
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(getMinecraftVersion()), this);
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
                    String logName = Util.getToday();
                    if (!Util.logName.equals(logName)) {
                        Log.updateLog(logName);
                        Util.processOldLog();
                    }
                }
            }, 30 * 60 * 1000, 30 * 60 * 1000); //半小时检查一次
        }
    }

    @Override
    public void onDisable() {
        disable();
        logMessage(Language.disableMessage);
    }

    public void disable() {
        UpdateChecker.stop();
        Log.closeAllLog();
    }

    public static ServerMonitor getPlugin() {
        return plugin;
    }

    public ClassLoader classLoader() {
        return getClassLoader();
    }

    public void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + message);
    }

    public int getMinecraftVersion() {
        return Integer.parseInt(getServer().getBukkitVersion().replace('-', '.').split("\\.")[1]);
    }

}
