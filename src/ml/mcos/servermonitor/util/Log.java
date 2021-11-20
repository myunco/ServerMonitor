package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.Language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;

public class Log {
    static BufferedWriter chatLog;
    static BufferedWriter commandLog;
    static BufferedWriter gameModeLog;
    static BufferedWriter opChangeLog;
    static BufferedWriter joinLeaveLog;
    static BufferedWriter warningLog;
    static HashMap<String, BufferedWriter> playerChatLog = new HashMap<>();
    static HashMap<String, BufferedWriter> playerCommandLog = new HashMap<>();
    static HashMap<String, BufferedWriter> playerGameModeLog = new HashMap<>();
    static ServerMonitor plugin = ServerMonitor.plugin;
    static File dateFolder = plugin.getDataFolder();

    public static BufferedWriter openLogFile(File file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8), 1024);
    }

    public static void checkParentFolder(File file, String message) {
        if (file.getParentFile().exists()) {
            return;
        }
        if (!file.getParentFile().mkdirs()) {
            ServerMonitor.consoleSender.sendMessage(message);
        }
    }

    public static void createChatLog() {
        File file = new File(dateFolder, "ChatLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> mkdirs error");
        try {
            chatLog = openLogFile(file);
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeChatLog(String str) {
        if (chatLog == null) {
            createChatLog();
        }
        try {
            chatLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                chatLog.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closeChatLog() {
        if (chatLog != null) {
            try {
                chatLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
            }
            chatLog = null;
        }
    }

    public static void createCommandLog() {
        File file = new File(dateFolder, "CommandLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> mkdirs error");
        try {
            commandLog = openLogFile(file);
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeCommandLog(String str) {
        if (commandLog == null) {
            createCommandLog();
        }
        try {
            commandLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                commandLog.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closeCommandLog() {
        if (commandLog != null) {
            try {
                commandLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
            }
            commandLog = null;
        }
    }

    public static void createGameModeLog() {
        File file = new File(dateFolder, "GameModeLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> mkdirs error");
        try {
            gameModeLog = openLogFile(file);
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeGameModeLog(String str) {
        if (gameModeLog == null) {
            createGameModeLog();
        }
        try {
            gameModeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                gameModeLog.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closeGameModeLog() {
        if (gameModeLog != null) {
            try {
                gameModeLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
            }
            gameModeLog = null;
        }
    }

    public static void createOpChangeLog() {
        try {
            opChangeLog = openLogFile(new File(dateFolder, "OpChange.log"));
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "OpChange.log"), e.getMessage());
        }
    }

    public static void writeOpChangeLog(String str) {
        if (opChangeLog == null) {
            createOpChangeLog();
        }
        try {
            opChangeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                opChangeLog.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "OpChange.log"), e.getMessage());
        }
    }

    public static void closeOpChangeLog() {
        if (opChangeLog != null) {
            try {
                opChangeLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "OpChange.log"), e.getMessage());
            }
            opChangeLog = null;
        }
    }

    public static void createJoinLeaveLog() {
        File file = new File(dateFolder, "JoinLeaveLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "JoinLeave -> mkdirs error");
        try {
            joinLeaveLog = openLogFile(file);
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeJoinLeaveLog(String str) {
        if (joinLeaveLog == null) {
            createJoinLeaveLog();
        }
        try {
            joinLeaveLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                joinLeaveLog.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closeJoinLeaveLog() {
        if (joinLeaveLog != null) {
            try {
                joinLeaveLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
            }
            joinLeaveLog = null;
        }
    }

    public static BufferedWriter addPlayerChatLog(String playerName) {
        File file = new File(dateFolder, "ChatLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> " + playerName + " -> " + "mkdirs error");
        try {
            playerChatLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
        return playerChatLog.get(playerName);
    }

    public static void writePlayerChatLog(String playerName, String str) {
        BufferedWriter writer = playerChatLog.get(playerName);
        if (writer == null) {
            writer = addPlayerChatLog(playerName);
        }
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                writer.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void closePlayerChatLog(String playerName) {
        BufferedWriter writer = playerChatLog.remove(playerName);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
            }
        }
    }

    public static BufferedWriter addPlayerCommandLog(String playerName) {
        File file = new File(dateFolder, "CommandLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> " + playerName + " -> " + "mkdirs error");
        try {
            playerCommandLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
        return playerCommandLog.get(playerName);
    }

    public static void writePlayerCommandLog(String playerName, String str) {
        BufferedWriter writer = playerCommandLog.get(playerName);
        if (writer == null) {
            writer = addPlayerCommandLog(playerName);
        }
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                writer.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void closePlayerCommandLog(String playerName) {
        BufferedWriter writer = playerCommandLog.remove(playerName);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
            }
        }
    }

    public static BufferedWriter addPlayerGameModeLog(String playerName) {
        File file = new File(dateFolder, "GameModeLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> " + playerName + " -> " + "mkdirs error");
        try {
            playerGameModeLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
        return playerGameModeLog.get(playerName);
    }

    public static void writePlayerGameModeLog(String playerName, String str) {
        BufferedWriter writer = playerGameModeLog.get(playerName);
        if (writer == null) {
            writer = addPlayerGameModeLog(playerName);
        }
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave) {
                writer.flush();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void closePlayerGameModeLog(String playerName) {
        BufferedWriter writer = playerGameModeLog.remove(playerName);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
            }
        }
    }

    public static void createWarningLog() {
        try {
            warningLog = openLogFile(new File(dateFolder, "Warning.log"));
        } catch (IOException e) {
            Util.sendException(Language.messageOpenException.replace("{file}", "Warning.log"), e.getMessage());
        }
    }

    public static void writeWarningLog(String str) {
        if (warningLog == null) {
            createWarningLog();
        }
        try {
            warningLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                warningLog.flush();
        } catch (IOException e) {
            Util.sendException(Language.messageWriteException.replace("{file}", "Warning.log"), e.getMessage());
        }
    }

    public static void closeWarningLog() {
        if (warningLog != null) {
            try {
                warningLog.close();
            } catch (IOException e) {
                Util.sendException(Language.messageCloseException.replace("{file}", "Warning.log"), e.getMessage());
            }
            warningLog = null;
        }
    }

    public static void closeAllLog() {
        closeChatLog();
        closeCommandLog();
        closeGameModeLog();
        closeOpChangeLog();
        closeJoinLeaveLog();
        if (Config.playerChat.get("perPlayer")) {
            new HashSet<>(playerChatLog.keySet()).forEach(Log::closePlayerChatLog);
        }
        if (Config.playerCommand.get("perPlayer")) {
            new HashSet<>(playerCommandLog.keySet()).forEach(Log::closePlayerCommandLog);
        }
        if (Config.playerGameModeChange.get("perPlayer")) {
            new HashSet<>(playerGameModeLog.keySet()).forEach(Log::closePlayerGameModeLog);
        }
    }

    public static void updateLog(String logName) {
        closeAllLog();
        Util.logName = logName;
    }

    public static void flushAllLog() {
        if (chatLog != null) {
            try {
                chatLog.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageWriteException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
            }
        }
        if (commandLog != null) {
            try {
                commandLog.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageWriteException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
            }
        }
        if (gameModeLog != null) {
            try {
                gameModeLog.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
            }
        }
        if (opChangeLog != null) {
            try {
                opChangeLog.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageWriteException.replace("{file}", "OpChange.log"), e.getMessage());
            }
        }
        if (joinLeaveLog != null) {
            try {
                joinLeaveLog.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageWriteException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
            }
        }
        if (Config.playerChat.get("perPlayer")) {
            playerChatLog.forEach((player, writer) -> {
                try {
                    writer.flush();
                } catch (IOException e) {
                    Util.sendException(Language.messageWriteException.replace("{file}", "Chat -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
                }
            });
        }
        if (Config.playerCommand.get("perPlayer")) {
            playerCommandLog.forEach((player, writer) -> {
                try {
                    writer.flush();
                } catch (IOException e) {
                    Util.sendException(Language.messageWriteException.replace("{file}", "Command -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
                }
            });
        }
        if (Config.playerGameModeChange.get("perPlayer")) {
            playerGameModeLog.forEach((player, writer) -> {
                try {
                    writer.flush();
                } catch (IOException e) {
                    Util.sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
                }
            });
        }
    }
}
