package com.github.myunco.servermonitor.config;

import com.github.myunco.servermonitor.ServerMonitor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.Set;

public class ConfigLoader {
    public static boolean error;
    private static ServerMonitor pl = ServerMonitor.plugin;

    public static void load() {
        pl.saveDefaultConfig();
        FileConfiguration config = pl.getConfig();
        Config.language = config.getString("language");
        if (Config.language == null) {
            loadError("配置文件错误! 请检查 language 是否存在.");
            return;
        }
        loadLanguage(Config.language);
        Config.dateFormat = config.getString("dateFormat");
        if (Config.dateFormat == null) {
            loadError("配置文件错误! 请检查 dateFormat 是否存在.");
            return;
        }
        Config.lineSeparator = config.getString("lineSeparator");
        if (Config.lineSeparator == null) {
            loadError("配置文件错误! 请检查 lineSeparator 是否存在.");
            return;
        }
        Config.realTimeSave = config.getBoolean("realTimeSave");
        Config.playerChat.put("playerChat", config.getBoolean("playerChat.enable"));
        Config.playerChat.put("perPlayer", config.getBoolean("playerChat.perPlayer"));
        Config.playerCommand.put("playerCommand", config.getBoolean("playerCommand.enable"));
        Config.playerCommand.put("perPlayer", config.getBoolean("playerCommand.perPlayer"));
        Config.playerGameModeChange.put("playerGameModeChange", config.getBoolean("playerGameModeChange.enable"));
        Config.playerGameModeChange.put("perPlayer", config.getBoolean("playerGameModeChange.perPlayer"));
        Config.opChange = config.getBoolean("opChange");
        Config.joinAndLeave = config.getBoolean("joinAndLeave");
        Config.whitelist = config.getStringList("whitelist");
        Config.alertCommandList = config.getStringList("alertCommandList");
        ConfigurationSection cs = config.getConfigurationSection("handleMethod");
        if (cs == null) {
            loadError("配置文件错误! 请检查 handleMethod 是否存在.");
            return;
        }
        Set<String> s = cs.getKeys(false);
        if (s.size() < 8) {
            loadError("配置文件错误! 请检查 handleMethod 下的所有项是否存在.");
            return;
        }
        for (String value : s) {
            if (value.equals("method")) {
                Config.handleMethod = config.getInt("handleMethod.method");
                continue;
            }
            Config.handleMethodConfig.put(value, config.getStringList("handleMethod." + value));
        }
    }

    public static void loadLanguage(String language) {
        File file = new File(pl.getDataFolder(), "languages" + File.separator + language + ".yml");
        if (!file.exists())
            System.out.println("语言文件: " + file.getAbsolutePath() + " 不存在.");
    }

    public static void loadError(String msg) {
        pl.getLogger().warning(msg);
        error = true;
    }
}
