package ml.mcos.servermonitor;

import ml.mcos.servermonitor.command.CommandServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.listener.PluginEventListener;
import ml.mcos.servermonitor.metrics.Metrics;
import ml.mcos.servermonitor.update.UpdateChecker;
import ml.mcos.servermonitor.util.Log;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerMonitor extends JavaPlugin {
    private static ServerMonitor plugin;
    private int mcVersion;
    private ConsoleCommandSender consoleSender;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = getMinecraftVersion();
        consoleSender = getServer().getConsoleSender();
        Config.loadConfig();
        PluginCommand command = getCommand("ServerMonitor");
        if (command != null) {
            command.setExecutor(new CommandServerMonitor());
            command.setTabCompleter((TabCompleter) command.getExecutor());
        }
        getServer().getPluginManager().registerEvents(new PluginEventListener(mcVersion), this);
        new Metrics(this, 12934);
        logMessage(Language.enabled);
    }

    public static ServerMonitor getPlugin() {
        return plugin;
    }

    public int getMcVersion() {
        return mcVersion;
    }

    public ClassLoader classLoader() {
        return getClassLoader();
    }

    public void enable() {
        Util.processOldLog();
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
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
            UpdateChecker.start();
        }
    }

    @Override
    public void onDisable() {
        disable();
        logMessage(Language.disabled);
    }

    public void disable() {
        getServer().getScheduler().cancelTasks(this);
        UpdateChecker.stop();
        Log.closeAllLog();
    }

    public void logMessage(String message) {
        consoleSender.sendMessage(Language.logPrefix + message);
    }

    public int getMinecraftVersion() {
        return Integer.parseInt(getServer().getBukkitVersion().replace('-', '.').split("\\.")[1]);
    }

}
