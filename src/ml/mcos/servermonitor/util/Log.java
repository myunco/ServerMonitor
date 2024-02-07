package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Log {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static final File dataFolder = plugin.getDataFolder();
    public static Logger chatLog = new Logger(new File(dataFolder, "ChatLogs"), "Chat -> ");
    public static Logger commandLog = new Logger(new File(dataFolder, "CommandLogs"), "Command -> ");
    public static Logger gameModeLog = new Logger(new File(dataFolder, "GameModeLogs"), "GameMode -> ");
    public static Logger opChangeLog = new SingleLogger(dataFolder, "OpChange.log");
    public static Logger joinLeaveLog = new Logger(new File(dataFolder, "JoinLeaveLogs"), "JoinLeave -> ");
    public static Logger warningLog = new SingleLogger(dataFolder, "Warning.log");
    public static Logger keywordsAlert = new SingleLogger(dataFolder, "KeywordsAlert.log");
    private static final HashMap<String, Logger> playerChatLog = new HashMap<>();
    private static final HashMap<String, Logger> playerCommandLog = new HashMap<>();
    private static final HashMap<String, Logger> playerGameModeLog = new HashMap<>();

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
