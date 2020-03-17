package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.executor.PluginCommandExecutor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    public static FileWriter chatLog;
    public static FileWriter commandLog;
    public static FileWriter gameModeLog;
    public static FileWriter opChangeLog;
    public static FileWriter joinLeaveLog;
    public static FileWriter warningLog;

    public static File dateFolder = ServerMonitor.plugin.getDataFolder();

    public static void createWarningLog() throws IOException {
        if (warningLog != null)
            return;
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
        if (chatLog != null)
            return;
        File file = new File(dateFolder, "ChatLogs/Chat.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        chatLog = new FileWriter(file, true);
    }

    public static void createCommandLog() throws IOException {
        if (commandLog != null)
            return;
        File file = new File(dateFolder, "CommandLogs/Command.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        commandLog = new FileWriter(file, true);
    }

    public static void createGameModeLog() throws IOException {
        if (gameModeLog != null)
            return;
        File file = new File(dateFolder, "GameModeLogs/GameMode.log");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        gameModeLog = new FileWriter(file, true);
    }

    public static void createOpChangeLog() throws IOException {
        if (opChangeLog != null)
            return;
        opChangeLog = new FileWriter(new File(dateFolder, "OpChange.log"), true);
    }

    public static void createJoinLeaveLog() throws IOException {
        if (joinLeaveLog != null)
            return;
        joinLeaveLog = new FileWriter(new File(dateFolder, "JoinAndLeave.log"), true);
    }

    public static void closeAllLog() throws IOException {
        if (chatLog != null)
            chatLog.close();
        if (commandLog != null)
            commandLog.close();
        if (gameModeLog != null)
            gameModeLog.close();
        if (opChangeLog != null)
            opChangeLog.close();
        if (joinLeaveLog != null)
            joinLeaveLog.close();
    }

    public static void sendException(String message, String exceptionMsg) {
        Bukkit.getConsoleSender().sendMessage(PluginCommandExecutor.MSG_PREFIX + message);
        Bukkit.getConsoleSender().sendMessage(PluginCommandExecutor.MSG_PREFIX + "Message:" + exceptionMsg);
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

}
