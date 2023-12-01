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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

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

    public Collection<? extends Player> getOnlinePlayers() {
        if (getMcVersion() > 7 || (getMcVersion() == 7 && getMcVersionPatch() == 10)) {
            return getServer().getOnlinePlayers();
        }
        try {
            return Arrays.asList((Player[]) Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers").invoke(plugin.getServer()));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void logMessage(String message) {
        getServer().getConsoleSender().sendMessage(Language.logPrefix + message);
    }

    public int getMinecraftVersion() {
        //return Integer.parseInt(getServer().getBukkitVersion().replace('-', '.').split("\\.")[1]);
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

    public int getMcVersionPatch() {
        return mcVersionPatch;
    }
}
