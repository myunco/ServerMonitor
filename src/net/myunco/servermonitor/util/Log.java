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
        if (Config.playerChat.get("enable")) {
            chatLog = new Logger(new File(dataFolder, "ChatLogs"), "Chat -> ");
        }
        if (Config.playerCommand.get("enable")) {
            commandLog = new Logger(new File(dataFolder, "CommandLogs"), "Command -> ");
        }
        if (Config.playerGameModeChange.get("enable")) {
            gameModeLog = new Logger(new File(dataFolder, "GameModeLogs"), "GameMode -> ");
        }
        if (Config.opChange) {
            opChangeLog = new SingleLogger(dataFolder, "OpChange.log");
        }
        if (Config.dbEnable) {
            dataSource = MySQL.getInstance();
            chatLog.setDataSource(dataSource);
            commandLog.setDataSource(dataSource);
            gameModeLog.setDataSource(dataSource);
            opChangeLog.setDataSource(dataSource);
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
        /*
        ArrayList<Logger> loggers = new ArrayList<>(playerChatLog.size() + 1);
        for (Map.Entry<String, Logger> entry : playerChatLog.entrySet()) {
            loggers.add(entry.getValue());
        }
        playerChatLog.clear();
        for (Logger logger : loggers) {
            logger.close();
        }
        loggers.clear();
        */
        // 似乎没有clear的必要，流close就行。玩家很多的服务器应该都会定时重启吧？
        for (Map.Entry<String, Logger> entry : playerChatLog.entrySet()) {
            entry.getValue().close();
        }
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
    }
    public static void closePlayerGameModeLog(String playerName) {
        Logger logger = playerGameModeLog.get(playerName);
        if (logger != null) {
            logger.close();
        }
    }

    public static void closeAllLog() {
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
        if (dataSource != null) {
            dataSource.closeConnection();
            dataSource = null;
        }
    }

    public static void updateLog(String logName) {
        Util.logName = logName;
        closeAllLog();
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
