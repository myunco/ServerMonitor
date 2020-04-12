package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.Language;
import org.bukkit.command.ConsoleCommandSender;

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
    public static ConsoleCommandSender consoleSender = ServerMonitor.consoleSender;

    public static void createWarningLog() throws IOException {
        warningLog = new FileWriter(new File(dateFolder, "warning.log"), true);
    }

    public static void writeWarningLog(String str) {
        try {
            warningLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                warningLog.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写WarningLog时发生IO异常!", e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "warning.log"), e.getMessage());
        }
    }

    public static void closeWarningLog() throws IOException {
        if (warningLog != null)
            warningLog.close();
    }

    public static void createChatLog() throws IOException {
        //File file = new File(dateFolder, "ChatLogs/Chat.log");
        File file = new File(dateFolder, "ChatLogs/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "Chat -> mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> mkdirs error");
        chatLog = new FileWriter(file, true);
    }

    public static void createCommandLog() throws IOException {
        //File file = new File(dateFolder, "CommandLogs/Command.log");
        File file = new File(dateFolder, "CommandLogs/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "Command -> mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> mkdirs error");
        commandLog = new FileWriter(file, true);
    }

    public static void createGameModeLog() throws IOException {
        //File file = new File(dateFolder, "GameModeLogs/GameMode.log");
        File file = new File(dateFolder, "GameModeLogs/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "GameMode -> mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> mkdirs error");
        gameModeLog = new FileWriter(file, true);
    }

    public static void createOpChangeLog() throws IOException {
        opChangeLog = new FileWriter(new File(dateFolder, "OpChange.log"), true);
    }

    public static void createJoinLeaveLog() throws IOException {
        //joinLeaveLog = new FileWriter(new File(dateFolder, "JoinAndLeave.log"), true);
        File file = new File(dateFolder, "JoinLeaveLogs/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "JoinLeave -> mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "JoinLeave -> mkdirs error");
        joinLeaveLog = new FileWriter(file, true);
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
            //sendException("§4[错误] §5在关闭ChatLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "Chat.log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (commandLog != null)
                commandLog.close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭CommandLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "Command.log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (gameModeLog != null)
                gameModeLog.close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭GameModeLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "GameMode.log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
        try {
            if (opChangeLog != null)
                opChangeLog.close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭OpChangeLog时发生IO异常!", e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "OpChange.log"), e.getMessage());
        }
        try {
            if (joinLeaveLog != null)
                joinLeaveLog.close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭JoinAndLeaveLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "JoinAndLeave.log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
        playerChatLog.keySet().forEach(player -> {
            try {
                playerChatLog.get(player).close();
            } catch (IOException e) {
                //sendException("§4[错误] §5在关闭" + "ChatLogs->" + value + ".log时发生IO异常!", e.getMessage());
                //sendException(Language.messageCloseException.replace("{file}", "ChatLogs->" + player + ".log"), e.getMessage());
                sendException(Language.messageCloseException.replace("{file}", "Chat -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
            }
        });
        playerCommandLog.keySet().forEach(player -> {
            try {
                playerCommandLog.get(player).close();
            } catch (IOException e) {
                //sendException("§4[错误] §5在关闭" + "CommandLogs->" + value + ".log时发生IO异常!", e.getMessage());
                //sendException(Language.messageCloseException.replace("{file}", "CommandLogs->" + player + ".log"), e.getMessage());
                sendException(Language.messageCloseException.replace("{file}", "Command -> " + player + " -> " + Util.logName + ".log"), e.getMessage());
            }
        });
        playerGameModeLog.keySet().forEach(player -> {
            try {
                playerGameModeLog.get(player).close();
            } catch (IOException e) {
                //sendException("§4[错误] §5在关闭" + "GameModeLogs->" + value + ".log时发生IO异常!", e.getMessage());
                //sendException(Language.messageCloseException.replace("{file}", "GameModeLogs->" + player + ".log"), e.getMessage());
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
            //sendException("§4[错误] §5在写ChatLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "Chat.log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "Chat -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeCommandLog(String str) {
        try {
            commandLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                commandLog.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写CommandLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "Command.log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "Command -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeGameModeLog(String str) {
        try {
            gameModeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                gameModeLog.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写GameModeLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "GameMode.log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void writeOpChangeLog(String str) {
        try {
            opChangeLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                opChangeLog.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写OpChangeLog时发生IO异常!", e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "OpChange.log"), e.getMessage());
        }
    }

    public static void writeJoinLeaveLog(String str) {
        try {
            joinLeaveLog.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                joinLeaveLog.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写JoinLeaveLog时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "JoinAndLeave.log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "JoinLeave -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void addPlayerChatLog(String playerName) {
        //File file = new File(dateFolder, "ChatLogs/players/" + playerName + ".log");
        File file = new File(dateFolder, "ChatLogs/players/" + playerName + "/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "Chat -> " + playerName + " -> " + "mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "Chat -> " + playerName + " -> " + "mkdirs error");
        try {
            playerChatLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            //sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void addPlayerCommandLog(String playerName) {
        //File file = new File(dateFolder, "CommandLogs/players/" + playerName + ".log");
        File file = new File(dateFolder, "CommandLogs/players/" + playerName + "/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "Command -> " + playerName + " -> " + "mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "Command -> " + playerName + " -> " + "mkdirs error");
        try {
            playerCommandLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            //sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void addPlayerGameModeLog(String playerName) {
        //File file = new File(dateFolder, "GameModeLogs/players/" + playerName + ".log");
        File file = new File(dateFolder, "GameModeLogs/players/" + playerName + "/" + Util.logName + ".log");
        /*if (!file.getParentFile().exists()) {
            boolean b = file.getParentFile().mkdirs();
            if (!b) consoleSender.sendMessage(Language.MSG_PREFIX + "GameMode -> " + playerName + " -> " + "mkdirs error");
        }*/
        checkParentFolder(file, Language.MSG_PREFIX + "GameMode -> " + playerName + " -> " + "mkdirs error");
        try {
            playerGameModeLog.put(playerName, new FileWriter(file, true));
        } catch (IOException e) {
            //sendException("§4[错误] §5在打开" + file.getPath() + "时发生IO异常!", e.getMessage());
            sendException(Language.messageOpenException.replace("{file}", file.getPath()), e.getMessage());
        }
    }

    public static void writePlayerChatLog(String playerName, String str) {
        FileWriter fw = playerChatLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写ChatLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "ChatLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //sendException("ChatLogs -> " + playerName + " -> NullPointerException", e.getMessage());
            //addPlayerChatLog(playerName);
        }
    }

    public static void writePlayerCommandLog(String playerName, String str) {
        FileWriter fw = playerCommandLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写CommandLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "CommandLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //sendException("CommandLogs -> " + playerName + " -> NullPointerException", e.getMessage());
            //addPlayerCommandLog(playerName);
        }
    }

    public static void writePlayerGameModeLog(String playerName, String str) {
        FileWriter fw = playerGameModeLog.get(playerName);
        try {
            fw.write(str + Config.lineSeparator);
            if (Config.realTimeSave)
                fw.flush();
        } catch (IOException e) {
            //sendException("§4[错误] §5在写GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageWriteException.replace("{file}", "GameModeLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageWriteException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        } catch (NullPointerException e) {
            //为什么只有这里会出现空指针，不应该啊
            //我测试没有遇到过，但有个用户反馈说会突然自己出现报错：
            /*
            [18:55:09 ERROR]: Could not pass event PlayerGameModeChangeEvent to ServerMonitor v1.0.0
            java.lang.NullPointerException: null
            at com.github.myunco.servermonitor.util.Log.writePlayerGameModeLog(Log.java:321) ~[?:?] //现在的325行
            at com.github.myunco.servermonitor.listener.PluginEventListener.playerGameModeChangeEvent(PluginEventListener.java:167) ~[?:?]
            at com.destroystokyo.paper.event.executor.asm.generated.GeneratedEventExecutor147.execute(Unknown Source) ~[?:?]
            at org.bukkit.plugin.EventExecutor.lambda$create$1(EventExecutor.java:69) ~[patched_1.14.4.jar:git-Paper-243]
            at co.aikar.timings.TimedEventExecutor.execute(TimedEventExecutor.java:80) ~[patched_1.14.4.jar:git-Paper-243]
            at org.bukkit.plugin.RegisteredListener.callEvent(RegisteredListener.java:70) ~[patched_1.14.4.jar:git-Paper-243]
            at org.bukkit.plugin.SimplePluginManager.callEvent(SimplePluginManager.java:545) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.EntityPlayer.a(EntityPlayer.java:1513) ~[patched_1.14.4.jar:git-Paper-243]
            at org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer.setGameMode(CraftPlayer.java:1062) ~[patched_1.14.4.jar:git-Paper-243]
            at net.citizensnpcs.trait.GameModeTrait.run(GameModeTrait.java:27) ~[?:?]
            at net.citizensnpcs.api.npc.AbstractNPC.update(AbstractNPC.java:430) ~[?:?]
            at net.citizensnpcs.npc.CitizensNPC.update(CitizensNPC.java:316) ~[?:?]
            at net.citizensnpcs.nms.v1_14_R1.entity.EntityHumanNPC.tick(EntityHumanNPC.java:439) ~[?:?]
            at net.minecraft.server.v1_14_R1.WorldServer.entityJoinedWorld(WorldServer.java:702) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.World.a(World.java:936) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.WorldServer.doTick(WorldServer.java:472) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.MinecraftServer.b(MinecraftServer.java:1231) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.DedicatedServer.b(DedicatedServer.java:417) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.MinecraftServer.a(MinecraftServer.java:1098) ~[patched_1.14.4.jar:git-Paper-243]
            at net.minecraft.server.v1_14_R1.MinecraftServer.run(MinecraftServer.java:925) ~[patched_1.14.4.jar:git-Paper-243]
            at java.lang.Thread.run(Unknown Source) [?:1.8.0_241]
            不清楚什么原因，都抓一下吧...
             */
            //sendException("GameModeLogs -> " + playerName + " -> NullPointerException", e.getMessage());
            //addPlayerGameModeLog(playerName);
            /*补充：我知道了···
            citizens这个插件生成的NPC也算作玩家，NPC游戏模式修改会触发这个事件...然后HashMap中肯定没有这个NPC的日志文件对象引用，所以NPE了。
            那就不sendException和addLog了，没在HashMap中的playerName都算作NPC处理。
             */
        }
    }

    public static void closePlayerChatLog(String playerName) {
        try {
            playerChatLog.get(playerName).close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭ChatLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "ChatLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "Chat -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closePlayerCommandLog(String playerName) {
        try {
            playerCommandLog.get(playerName).close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭CommandLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "CommandLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "Command -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void closePlayerGameModeLog(String playerName) {
        try {
            playerGameModeLog.get(playerName).close();
        } catch (IOException e) {
            //sendException("§4[错误] §5在关闭GameModeLogs->" + playerName + ".log时发生IO异常!", e.getMessage());
            //sendException(Language.messageCloseException.replace("{file}", "GameModeLogs->" + playerName + ".log"), e.getMessage());
            sendException(Language.messageCloseException.replace("{file}", "GameMode -> " + playerName + " -> " + Util.logName + ".log"), e.getMessage());
        }
    }

    public static void checkParentFolder(File file, String message) {
        if (file.getParentFile().exists())
            return;
        boolean ret = file.getParentFile().mkdirs();
        if (!ret) consoleSender.sendMessage(message);
    }
}
