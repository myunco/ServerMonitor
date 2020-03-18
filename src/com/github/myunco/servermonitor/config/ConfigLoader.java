package com.github.myunco.servermonitor.config;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.util.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ConfigLoader {
    private static ServerMonitor pl = ServerMonitor.plugin;

    public static boolean load() {
        pl.saveDefaultConfig();
        FileConfiguration config = pl.getConfig();
        Config.language = config.getString("language");
        if (Config.language == null) {
            loadError("配置文件错误! 请检查 language 是否存在.");
            return false;
        }
        loadLanguage(Config.language);
        Config.dateFormat = config.getString("dateFormat");
        if (Config.dateFormat == null) {
            loadError("配置文件错误! 请检查 dateFormat 是否存在.");
            return false;
        }
        Config.lineSeparator = config.getString("lineSeparator");
        if (Config.lineSeparator == null) {
            loadError("配置文件错误! 请检查 lineSeparator 是否存在.");
            return false;
        }
        if (Config.lineSeparator.toLowerCase().equals("auto"))
            Config.lineSeparator = System.lineSeparator();
        Config.realTimeSave = config.getBoolean("realTimeSave");
        Config.playerChat.put("enable", config.getBoolean("playerChat.enable"));
        Config.playerChat.put("perPlayer", config.getBoolean("playerChat.perPlayer"));
        Config.playerCommand.put("enable", config.getBoolean("playerCommand.enable"));
        Config.playerCommand.put("perPlayer", config.getBoolean("playerCommand.perPlayer"));
        Config.playerCommand.put("consoleCommand", config.getBoolean("playerCommand.consoleCommand"));
        Config.playerGameModeChange.put("enable", config.getBoolean("playerGameModeChange.enable"));
        Config.playerGameModeChange.put("perPlayer", config.getBoolean("playerGameModeChange.perPlayer"));
        Config.opChange = config.getBoolean("playerCommand.opChange");
        Config.joinAndLeave = config.getBoolean("joinAndLeave");
        Config.commandAlert = config.getBoolean("commandAlert.enable");
        if (Config.commandAlert) {
            Config.whitelist = config.getStringList("commandAlert.whitelist");
            Config.cancel = config.getBoolean("commandAlert.cancel");
            Config.commandWhiteList = config.getStringList("commandAlert.commandWhiteList");
            ConfigurationSection cs = config.getConfigurationSection("commandAlert.handleMethod");
            if (cs == null) {
                loadError("配置文件错误! 请检查 commandAlert下的handleMethod 是否存在.");
                return false;
            }
            Set<String> s = cs.getKeys(false);
            if (s.size() < 8) {
                loadError("配置文件错误! 请检查 commandAlert下的handleMethod下的所有项 是否存在.");
                return false;
            }
            for (String value : s) {
                if (value.equals("method")) {
                    Config.handleMethod = config.getInt("commandAlert.handleMethod.method");
                    continue;
                }
                Config.handleMethodConfig.put(value, config.getStringList("commandAlert.handleMethod." + value));
            }
        }
        if (Config.playerChat.get("enable")) {
            try {
                Log.createChatLog();
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开ChatLog时发生IO异常!", e.getMessage());
                return false;
            }
        }
        if (Config.playerCommand.get("enable")) {
            try {
                Log.createCommandLog();
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开CommandLog时发生IO异常!", e.getMessage());
                return false;
            }
        }
        if (Config.playerGameModeChange.get("enable")) {
            try {
                Log.createGameModeLog();
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开GameModeLog时发生IO异常!", e.getMessage());
                return false;
            }
        }
        if (Config.opChange) {
            try {
                Log.createOpChangeLog();
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开OpChangeLog时发生IO异常!", e.getMessage());
                return false;
            }
        }
        if (Config.joinAndLeave) {
            try {
                Log.createJoinLeaveLog();
            } catch (IOException e) {
                Log.sendException("§4[错误] §5在打开JoinLeaveLog时发生IO异常!", e.getMessage());
                return false;
            }
        }
        pl.getServer().getOnlinePlayers().forEach(player -> {
            String playerName = player.getName();
            if (Config.playerChat.get("perPlayer"))
                Log.addPlayerChatLog(playerName);
            if (Config.playerCommand.get("perPlayer"))
                Log.addPlayerCommandLog(playerName);
            if (Config.playerGameModeChange.get("perPlayer"))
                Log.addPlayerGameModeLog(playerName);
        });
        return true;
    }

    public static void reload() {
        Log.closeAllLog();
        pl.reloadConfig();
        load();
    }

    public static void loadLanguage(String language) {
        File file = new File(pl.getDataFolder(), "languages/" + language + ".yml");
        if (!file.exists())
            System.out.println("[ServerMonitor] 语言文件: " + file.getPath() + " 不存在.");
    }

    public static void loadError(String msg) {
        pl.getLogger().warning(msg);
    }
}
