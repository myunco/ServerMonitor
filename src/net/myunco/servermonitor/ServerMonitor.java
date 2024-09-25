package net.myunco.servermonitor;

import net.myunco.servermonitor.command.CommandServerMonitor;
import net.myunco.servermonitor.config.Config;
import net.myunco.servermonitor.config.Language;
import net.myunco.servermonitor.listener.PluginEventListener;
import net.myunco.servermonitor.metrics.Metrics;
import net.myunco.servermonitor.update.UpdateChecker;
import net.myunco.servermonitor.update.UpdateNotification;
import net.myunco.servermonitor.util.Log;
import net.myunco.servermonitor.util.Util;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServerMonitor extends JavaPlugin {
    private static ServerMonitor plugin;
    private static Timer timer;
    private int mcVersion;
    private int mcVersionPatch;

    @Override
    public void onEnable() {
        plugin = this;
        mcVersion = getMinecraftVersion();
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

    public ArrayList<Player> getOnlineOperators() {
        ArrayList<Player> result = new ArrayList<>();
        for (OfflinePlayer operator : getServer().getOperators()) {
            Player player = getServer().getPlayer(operator.getUniqueId());
            if (player != null) {
                result.add(player);
            }
        }
        return result;
    }

    public int getMinecraftVersion() {
        String[] version = getServer().getBukkitVersion().replace('-', '.').split("\\.");
        try {
            mcVersionPatch = Integer.parseInt(version[2]);
        } catch (NumberFormatException ignored) {
        }
        return Integer.parseInt(version[1]);
    }

    public int getMcVersion() {
        return mcVersion;
    }

    public boolean isVersionGtOrEq(int version, int patch) {
        return mcVersion > version || mcVersion == version && mcVersionPatch >= patch;
    }

    public void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + message);
    }
}
