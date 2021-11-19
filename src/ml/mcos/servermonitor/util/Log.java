package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.Language;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Log {
    public static BufferedWriter chatLog;
    public static BufferedWriter commandLog;
    public static BufferedWriter gameModeLog;
    public static BufferedWriter opChangeLog;
    public static BufferedWriter joinLeaveLog;
    public static BufferedWriter warningLog;

    public static HashMap<String, BufferedWriter> playerChatLog = new HashMap<>();
    public static HashMap<String, BufferedWriter> playerCommandLog = new HashMap<>();
    public static HashMap<String, BufferedWriter> playerGameModeLog = new HashMap<>();

    public static File dateFolder = ServerMonitor.plugin.getDataFolder();
    public static ConsoleCommandSender consoleSender = ServerMonitor.consoleSender;

    public static BufferedWriter openLogFile(File file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8), 1024);
    }

    public static void createWarningLog() throws IOException {
        warningLog = openLogFile(new File(dateFolder, "Warning.log"));
    }

    public static void createCommandLog() throws IOException {
        File file = new File(dateFolder, "CommandLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> mkdirs error");
        commandLog = openLogFile(file);
    }

    public static void createGameModeLog() throws IOException {
        File file = new File(dateFolder, "GameModeLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> mkdirs error");
        gameModeLog = openLogFile(file);
    }

    public static void createOpChangeLog() throws IOException {
        opChangeLog = openLogFile(new File(dateFolder, "OpChange.log"));
    }

    public static void createJoinLeaveLog() throws IOException {
        File file = new File(dateFolder, "JoinLeaveLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "JoinLeave -> mkdirs error");
        joinLeaveLog = openLogFile(file);
    }

    public static void closeAllLog() {
        //能关一个尽量关一个
        try {
            if (chatLog != null)
                chatLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (commandLog != null)
                commandLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (gameModeLog != null)
                gameModeLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (opChangeLog != null)
                opChangeLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "OpChange.log"), e.getMessage());
        }
        try {
            if (joinLeaveLog != null)
                joinLeaveLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
        playerChatLog.keySet().forEach(player -> {
            try {
                playerChatLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "Chat -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
            }
        });
        playerCommandLog.keySet().forEach(player -> {
            try {
                playerCommandLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "Command -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
            }
        });
        playerGameModeLog.keySet().forEach(player -> {
            try {
                playerGameModeLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
            }
        });
    }

    public static void sendException(String message, String exceptionMsg) {
        consoleSender.sendMessage(Language.MSG_PREFIX + message);
        consoleSender.sendMessage(Language.MSG_PREFIX + "Message: " + exceptionMsg);
    }

    public static void writeChatLog(String str) {
        try {
            chatLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                chatLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeCommandLog(String str) {
        try {
            commandLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                commandLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeGameModeLog(String str) {
        try {
            gameModeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                gameModeLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeOpChangeLog(String str) {
        try {
            opChangeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                opChangeLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "OpChange.log"), e.getMessage());
        }
    }

    public static void writeJoinLeaveLog(String str) {
        try {
            joinLeaveLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                joinLeaveLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void addPlayerChatLog(String playerName) {
        File file = new File(dateFolder, "ChatLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> " + playerName + " -> " + "mkdirs error");
        try {
            playerChatLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void addPlayerCommandLog(String playerName) {
        File file = new File(dateFolder, "CommandLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> " + playerName + " -> " + "mkdirs error");
        try {
            playerCommandLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void addPlayerGameModeLog(String playerName) {
        File file = new File(dateFolder, "GameModeLogs/players/" + playerName + "/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> " + playerName + " -> " + "mkdirs error");
        try {
            playerGameModeLog.put(playerName, openLogFile(file));
        } catch (IOException e) {
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void writePlayerChatLog(String playerName, String str) {
        BufferedWriter writer = playerChatLog.get(playerName);
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                writer.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //没在HashMap中的playerName都算作NPC处理
        }
    }

    public static void writePlayerCommandLog(String playerName, String str) {
        BufferedWriter writer = playerCommandLog.get(playerName);
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                writer.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //没在HashMap中的playerName都算作NPC处理
        }
    }

    public static void writePlayerGameModeLog(String playerName, String str) {
        BufferedWriter writer = playerGameModeLog.get(playerName);
        try {
            writer.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                writer.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //没在HashMap中的playerName都算作NPC处理
        }
    }

    public static void closePlayerChatLog(String playerName) {
        try {
            playerChatLog.get(playerName).close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closePlayerCommandLog(String playerName) {
        try {
            playerCommandLog.get(playerName).close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closePlayerGameModeLog(String playerName) {
        try {
            playerGameModeLog.get(playerName).close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void checkParentFolder(File file, String message) {
        if (file.getParentFile().exists())
            return;
        if (!file.getParentFile().mkdirs()) {
            consoleSender.sendMessage(message);
        }
    }

    public static void writeWarningLog(String str) {
        try {
            warningLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                warningLog.flush();
        } catch (IOException e) {
            sendException(Language.messageWriteException.replace("{file}", "Warning.log"), e.getMessage());
        }
    }

    public static void closeWarningLog() throws IOException {
        if (warningLog != null)
            warningLog.close();
    }

    public static void createChatLog() throws IOException {
        File file = new File(dateFolder, "ChatLogs/" + Util.logName + ".log");
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> mkdirs error");
        chatLog = openLogFile(file);
    }

    public static boolean createAllLog(boolean all) {
        if (Config.playerChat.get("enable")) {
            try {
                Log.createChatLog();
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
                return false;
            }
        }
        if (Config.playerCommand.get("enable")) {
            try {
                Log.createCommandLog();
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
                return false;
            }
        }
        if (Config.playerGameModeChange.get("enable")) {
            try {
                Log.createGameModeLog();
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
                return false;
            }
        }
        if (all && Config.opChange) {
            try {
                Log.createOpChangeLog();
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "OpChange.log"), e.getMessage());
                return false;
            }
        }
        if (Config.joinAndLeave) {
            try {
                Log.createJoinLeaveLog();
            } catch (IOException e) {
                Log.sendException(Language.messageOpenException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
                return false;
            }
        }
        for (Player player : ServerMonitor.plugin.getOnlinePlayers()) {
            String playerName = player.getName();
            if (Config.playerChat.get("perPlayer"))
                Log.addPlayerChatLog(playerName);
            if (Config.playerCommand.get("perPlayer"))
                Log.addPlayerCommandLog(playerName);
            if (Config.playerGameModeChange.get("perPlayer"))
                Log.addPlayerGameModeLog(playerName);
        }
        return true;
    }

    public static void updateAllLog(String logName) {
        String tmp_logName = Util.logName;
        BufferedWriter tmp_chatLog = chatLog;
        BufferedWriter tmp_commandLog = commandLog;
        BufferedWriter tmp_gameModeLog = gameModeLog;
        BufferedWriter tmp_joinLeaveLog = joinLeaveLog;
        HashMap<String, BufferedWriter> tmp_playerChatLog = new HashMap<>(playerChatLog);
        HashMap<String, BufferedWriter> tmp_playerCommandLog = new HashMap<>(playerCommandLog);
        HashMap<String, BufferedWriter> tmp_playerGameModeLog = new HashMap<>(playerGameModeLog);
        Util.logName = logName;
        createAllLog(false);
        try {
            if (tmp_chatLog != null)
                tmp_chatLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Chat -> " + tmp_logName + ".log"), e.getMessage());
        }
        try {
            if (tmp_commandLog != null)
                tmp_commandLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "Command -> " + tmp_logName + ".log"), e.getMessage());
        }
        try {
            if (tmp_gameModeLog != null)
                tmp_gameModeLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + tmp_logName + ".log"), e.getMessage());
        }
        try {
            if (tmp_joinLeaveLog != null)
                tmp_joinLeaveLog.close();
        } catch (IOException e) {
            sendException(Language.messageCloseException.replace("{file}", "JoinLeave -> " + tmp_logName + ".log"), e.getMessage());
        }
        tmp_playerChatLog.keySet().forEach(player -> {
            try {
                tmp_playerChatLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "Chat -> " + player + " -> " + tmp_logName + ".log"), e.getMessage());
            }
        });
        tmp_playerCommandLog.keySet().forEach(player -> {
            try {
                tmp_playerCommandLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "Command -> " + player + " -> " + tmp_logName + ".log"), e.getMessage());
            }
        });
        tmp_playerGameModeLog.keySet().forEach(player -> {
            try {
                tmp_playerGameModeLog.get(player).close();
            } catch (IOException e) {
                sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + player + " -> " + tmp_logName + ".log"), e.getMessage());
            }
        });
    }

    public static void flushAllLog() {

    }
}
