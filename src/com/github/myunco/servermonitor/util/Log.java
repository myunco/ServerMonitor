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

    public static File dateFolder = ServerMonitor.plugin.getDataFolder();

    public static void warningLog(String str){
        FileWriter fw;
        try {
            fw = new FileWriter(new File(dateFolder, "warning.log"), true);
            try {
                fw.write(str + Config.lineSeparator);
                try {
                    fw.close();
                } catch (IOException e) {
                    sendException("§4[错误] §5在关闭warningLog时发生IO异常!", e.getMessage());
                }
            } catch (IOException e) {
                sendException("§4[错误] §5在写入warningLog时发生IO异常!", e.getMessage());
            }
        } catch (IOException e) {
            sendException("§4[错误] §5在打开warningLog时发生IO异常!", e.getMessage());
        }
    }

    public static void createChatLog() throws IOException{
        chatLog = new FileWriter(new File(dateFolder, "ChatLogs/Chat.log"), true);
    }

    public static void createCommandLog() throws IOException{
        commandLog = new FileWriter(new File(dateFolder, "CommandLogs/Command.log"), true);
    }

    public static void createGameModeLog() throws IOException{
        gameModeLog = new FileWriter(new File(dateFolder, "GameModeLogs/GameMode.log"), true);
    }

    public static void createOpChangeLog() throws IOException{
        opChangeLog = new FileWriter(new File(dateFolder, "OpChange.log"), true);
    }

    public static void createJoinLeaveLog() throws IOException{
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
}
