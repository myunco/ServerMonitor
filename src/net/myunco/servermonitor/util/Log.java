package net.myunco.servermonitor.util;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Config;
import net.myunco.servermonitor.database.DataSource;
import net.myunco.servermonitor.database.MySQL;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Log {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static final File dataFolder = plugin.getDataFolder();
    public static Logger chatLog;
    public static Logger commandLog;
    public static Logger gameModeLog;
    public static Logger opChangeLog;
    public static Logger joinLeaveLog;
    public static Logger warningLog;
    public static Logger keywordsAlert;
    private static final HashMap<String, Logger> playerChatLog = new HashMap<>();
    private static final HashMap<String, Logger> playerCommandLog = new HashMap<>();
    private static final HashMap<String, Logger> playerGameModeLog = new HashMap<>();
    private static DataSource dataSource;

    public static void init() {
        if (Config.dbEnable) {
            dataSource = MySQL.getInstance(); //TODO 先这么写 后面支持其他数据库再重写逻辑
        }
        if (Config.playerChat.get("enable")) {
            chatLog = new Logger(new File(dataFolder, "ChatLogs"), "Chat -> ", dataSource);
        }
        if (Config.playerCommand.get("enable")) {
            commandLog = new Logger(new File(dataFolder, "CommandLogs"), "Command -> ", dataSource);
        }
        if (Config.playerGameModeChange.get("enable")) {
            gameModeLog = new Logger(new File(dataFolder, "GameModeLogs"), "GameMode -> ", dataSource);
        }
        if (Config.opChange) {
            opChangeLog = new SingleLogger(dataFolder, "OpChange.log", dataSource);
        }
        if (Config.joinAndLeave) {
            joinLeaveLog = new Logger(new File(dataFolder, "JoinLeaveLogs"), "JoinLeave -> ", dataSource);
        }
        if (Config.commandAlertEnable) {
            warningLog = new SingleLogger(dataFolder, "Warning.log", dataSource);
        }
        if (Config.keywordsAlertEnable) {
            keywordsAlert = new SingleLogger(dataFolder, "KeywordsAlert.log", dataSource);
        }
    }

    public static void writePlayerChatLog(String playerName, String msg) {
        playerChatLog.computeIfAbsent(playerName,
                key -> new Logger(new File(dataFolder, "ChatLogs/players/" + playerName), "Chat -> " + playerName + " -> ")).write(msg);
    }

    public static void closePlayerChatLog() {
        for (Map.Entry<String, Logger> entry : playerChatLog.entrySet()) {
            entry.getValue().close();
        }
        playerChatLog.clear();
    }

    public static void closePlayerChatLog(String playerName) {
        Logger logger = playerChatLog.get(playerName);
        if (logger != null) {
            logger.close();
        }
    }

    public static void writePlayerCommandLog(String playerName, String msg) {
        playerCommandLog.computeIfAbsent(playerName,
                key -> new Logger(new File(dataFolder, "CommandLogs/players/" + playerName), "Command -> " + playerName + " -> ")).write(msg);
    }

    public static void closePlayerCommandLog() {
        for (Map.Entry<String, Logger> entry : playerCommandLog.entrySet()) {
            entry.getValue().close();
        }
        playerCommandLog.clear();
    }

    public static void closePlayerCommandLog(String playerName) {
        Logger logger = playerCommandLog.get(playerName);
        if (logger != null) {
            logger.close();
        }
    }

    public static void writePlayerGameModeLog(String playerName, String msg) {
        playerGameModeLog.computeIfAbsent(playerName,
                key -> new Logger(new File(dataFolder, "GameModeLogs/players/" + playerName), "GameMode -> " + playerName + " -> ")).write(msg);
    }

    public static void closePlayerGameModeLog() {
        for (Map.Entry<String, Logger> entry : playerGameModeLog.entrySet()) {
            entry.getValue().close();
        }
        playerGameModeLog.clear();
    }
    public static void closePlayerGameModeLog(String playerName) {
        Logger logger = playerGameModeLog.get(playerName);
        if (logger != null) {
            logger.close();
        }
    }

    public static void closeAllLog(boolean closeDataSource) {
        chatLog.close();
        commandLog.close();
        gameModeLog.close();
        opChangeLog.close();
        joinLeaveLog.close();
        if (Config.playerChat.get("perPlayer")) {
            closePlayerChatLog();
        }
        if (Config.playerCommand.get("perPlayer")) {
            closePlayerCommandLog();
        }
        if (Config.playerGameModeChange.get("perPlayer")) {
            closePlayerGameModeLog();
        }
        if (closeDataSource && dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public static void updateLog(String logName) {
        Util.logName = logName;
        closeAllLog(false);
    }

    public static void flushAllLog() {
        chatLog.flush();
        commandLog.flush();
        gameModeLog.flush();
        opChangeLog.flush();
        joinLeaveLog.flush();
        if (Config.playerChat.get("perPlayer")) {
            for (Map.Entry<String, Logger> entry : playerChatLog.entrySet()) {
                entry.getValue().flush();
            }
        }
        if (Config.playerCommand.get("perPlayer")) {
            for (Map.Entry<String, Logger> entry : playerCommandLog.entrySet()) {
                entry.getValue().flush();
            }
        }
        if (Config.playerGameModeChange.get("perPlayer")) {
            for (Map.Entry<String, Logger> entry : playerGameModeLog.entrySet()) {
                entry.getValue().flush();
            }
        }
    }

}
