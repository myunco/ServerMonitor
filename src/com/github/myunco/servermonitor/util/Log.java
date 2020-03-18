package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Log {
    public static FileWriter chatLog;
    public static FileWriter commandLog;
    public static FileWriter gameModeLog;
    public static FileWriter opChangeLog;
    public static FileWriter joinLeaveLog;
    public static FileWriter warningLog;

    public static HashMap<String, FileWriter> playerChatLog = new HashMap<>();
    public static HashMap<String, FileWriter> playerCommandLog = new HashMap<>();
    public static HashMap<String, FileWriter> playerGameModeLog = new HashMap<>();

    public static File dateFolder = ServerMonitor.plugin.getDataFolder();

    public static void createWarningLog() throws IOException {
        warningLog = new FileWriter(new File(dateFolder, "warning.log"), true);
    }

    public static void writeWarningLog(String str) {
        try {
            warningLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                warningLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写WarningLog时发生IO异常!", e.getMessage());
        }
    }

    public static void closeWarningLog() throws IOException {
        if (warningLog != null)
            warningLog.close();
    }

    public static void createChatLog() throws IOException {
        File file = new File(dateFolder, "ChatLogs/Chat.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        chatLog = new FileWriter(file, true);
    }

    public static void createCommandLog() throws IOException {
        File file = new File(dateFolder, "CommandLogs/Command.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        commandLog = new FileWriter(file, true);
    }

    public static void createGameModeLog() throws IOException {
        File file = new File(dateFolder, "GameModeLogs/GameMode.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        gameModeLog = new FileWriter(file, true);
    }

    public static void createOpChangeLog() throws IOException {
        opChangeLog = new FileWriter(new File(dateFolder, "OpChange.log"), true);
    }

    public static void createJoinLeaveLog() throws IOException {
        joinLeaveLog = new FileWriter(new File(dateFolder, "JoinAndLeave.log"), true);
    }

    public static void closeAllLog() {
        //本着能关一个尽量关一个的原则
        /*
        try {
            if (chatLog != null)
                chatLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭ChatLog时发生IO异常!", e.getMessage());
        } finally {
            try {
                if (commandLog != null)
                    commandLog.close();
            } catch (IOException e) {
                sendException("§4[错误] §5在关闭CommandLog时发生IO异常!", e.getMessage());
            } finally {
                try {
                    if (gameModeLog != null)
                        gameModeLog.close();
                } catch (IOException e) {
                    sendException("§4[错误] §5在关闭GameModeLog时发生IO异常!", e.getMessage());
                } finally {
                    try {
                        if (opChangeLog != null)
                            opChangeLog.close();
                    } catch (IOException e) {
                        sendException("§4[错误] §5在关闭OpChangeLog时发生IO异常!", e.getMessage());
                    } finally {
                        try {
                            if (joinLeaveLog != null)
                                joinLeaveLog.close();
                        } catch (IOException e) {
                            sendException("§4[错误] §5在关闭JoinAndLeaveLog时发生IO异常!", e.getMessage());
                        } finally {
                            playerChatLog.keySet().forEach(value -> {
                                try {
                                    playerChatLog.get(value).close();
                                } catch (IOException e) {
                                    sendException("§4[错误] §5在关闭" + "ChatLogs->" + value + ".log时发生IO异常!", e.getMessage());
                                }
                            });
                            playerCommandLog.keySet().forEach(value -> {
                                try {
                                    playerCommandLog.get(value).close();
                                } catch (IOException e) {
                                    sendException("§4[错误] §5在关闭" + "CommandLogs->" + value + ".log时发生IO异常!", e.getMessage());
                                }
                            });
                            playerGameModeLog.keySet().forEach(value -> {
                                try {
                                    playerGameModeLog.get(value).close();
                                } catch (IOException e) {
                                    sendException("§4[错误] §5在关闭" + "GameModeLogs->" + value + ".log时发生IO异常!", e.getMessage());
                                }
                            });
                        }
                    }
                }
            }
        }
        ????我为什么要这么写???
        */
        try {
            if (chatLog != null)
                chatLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭ChatLog时发生IO异常!", e.getMessage());
        }
        try {
            if (commandLog != null)
                commandLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭CommandLog时发生IO异常!", e.getMessage());
        }
        try {
            if (gameModeLog != null)
                gameModeLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭GameModeLog时发生IO异常!", e.getMessage());
        }
        try {
            if (opChangeLog != null)
                opChangeLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭OpChangeLog时发生IO异常!", e.getMessage());
        }
        try {
            if (joinLeaveLog != null)
                joinLeaveLog.close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭JoinAndLeaveLog时发生IO异常!", e.getMessage());
        }
        playerChatLog.keySet().forEach(value -> {
            try {
                playerChatLog.get(value).close();
            } catch (IOException e) {
                sendException("§4[错误] §5在关闭" + "ChatLogs->" + value + ".log时发生IO异常!", e.getMessage());
            }
        });
        playerCommandLog.keySet().forEach(value -> {
            try {
                playerCommandLog.get(value).close();
            } catch (IOException e) {
                sendException("§4[错误] §5在关闭" + "CommandLogs->" + value + ".log时发生IO异常!", e.getMessage());
            }
        });
        playerGameModeLog.keySet().forEach(value -> {
            try {
                playerGameModeLog.get(value).close();
            } catch (IOException e) {
                sendException("§4[错误] §5在关闭" + "GameModeLogs->" + value + ".log时发生IO异常!", e.getMessage());
            }
        });
    }

    public static void sendException(String message, String exceptionMsg) {
        Bukkit.getConsoleSender().sendMessage(PluginCommandExecutor.MSG_PREFIX + message);
        Bukkit.getConsoleSender().sendMessage(PluginCommandExecutor.MSG_PREFIX + "Message: " + exceptionMsg);
    }

    public static void writeChatLog(String str) {
        try {
            chatLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                chatLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写ChatLog时发生IO异常!", e.getMessage());
        }
    }

    public static void writeCommandLog(String str) {
        try {
            commandLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                commandLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写CommandLog时发生IO异常!", e.getMessage());
        }
    }

    public static void writeGameModeLog(String str) {
        try {
            gameModeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                gameModeLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写GameModeLog时发生IO异常!", e.getMessage());
        }
    }

    public static void writeOpChangeLog(String str) {
        try {
            opChangeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                opChangeLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写OpChangeLog时发生IO异常!", e.getMessage());
        }
    }

    public static void writeJoinLeaveLog(String str) {
        try {
            joinLeaveLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                joinLeaveLog.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写JoinLeaveLog时发生IO异常!", e.getMessage());
        }
    }

    public static void addPlayerChatLog(String playerName) {
        File file = new File(dateFolder, "ChatLogs/players/" + playerName + ".log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            playerChatLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
        }
    }

    public static void addPlayerCommandLog(String playerName) {
        File file = new File(dateFolder, "CommandLogs/players/" + playerName + ".log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            playerCommandLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
        }
    }

    public static void addPlayerGameModeLog(String playerName) {
        File file = new File(dateFolder, "GameModeLogs/players/" + playerName + ".log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            playerGameModeLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
        }
    }

    public static void writePlayerChatLog(String playerName, String str) {
        FileWriter fw = playerChatLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写ChatLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }

    public static void writePlayerCommandLog(String playerName, String str) {
        FileWriter fw = playerCommandLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写CommandLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }

    public static void writePlayerGameModeLog(String playerName, String str) {
        FileWriter fw = playerGameModeLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            sendException("§4[错误] §5在写GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }

    public static void closePlayerChatLog(String playerName) {
        try {
            playerChatLog.get(playerName).close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }

    public static void closePlayerCommandLog(String playerName) {
        try {
            playerCommandLog.get(playerName).close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }

    public static void closePlayerGameModeLog(String playerName) {
        try {
            playerGameModeLog.get(playerName).close();
        } catch (IOException e) {
            sendException("§4[错误] §5在关闭GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
        }
    }
}
