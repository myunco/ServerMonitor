package ml.mcos.servermonitor.config;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Language {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    public static int version;
    public static String logPrefix;
    public static String messagePrefix;
    public static String languageVersionError;
    public static String languageVersionOutdated;
    public static String languageUpdateComplete;
    public static String updateFoundNewVersion;
    public static String updateMajorUpdate;
    public static String updateDownloadLink;
    public static String updateCheckFailure;
    public static String updateCheckException;
    public static String enableMessage;
    public static String disableMessage;
    public static String commandHelp;
    public static String commandReload;
    public static String commandUnknown;
    public static String logPlayerChat;
    public static String logPlayerCommand;
    public static String logPlayerCommandOp;
    public static String logPlayerCommandNonOp;
    public static String logConsoleCommand;
    public static String logOpChangeOpPlayer;
    public static String logOpChangeDeopPlayer;
    public static String logOpChangeOpConsole;
    public static String logOpChangeDeopConsole;
    public static String logPlayerGameModeChange;
    public static String logPlayerJoin;
    public static String logPlayerQuit;
    public static String logPlayerKick;
    public static String messageException;
    public static String messageExceptionOpen;
    public static String messageExceptionWrite;
    public static String messageExceptionClose;
    public static String messageExceptionSave;
    public static String messageExceptionZip;
    public static String messageErrorDelete;

    public static void loadLanguage(String language) {
        if (language == null || !language.matches("[a-zA-Z]{2}[_-][a-zA-Z]{2}")) {
            plugin.getLogger().severe("§4语言文件名称格式错误: " + language);
            language = "zh_cn";
        }
        String langPath = "languages/" + language + ".yml";
        File lang = new File(plugin.getDataFolder(), langPath);
        saveDefaultLanguage(lang, langPath);
        YamlConfiguration config = Config.loadConfiguration(lang);
        version = config.getInt("version");
        logPrefix = config.getString("log-prefix", "§3[§aServerMonitor§3] ");
        messagePrefix = config.getString("message-prefix", "§3[§aServerMonitor§3] §e-> ");
        languageVersionError = config.getString("language-version-error", "§c语言文件版本错误: ");
        languageVersionOutdated = config.getString("language-version-outdated", "§e当前语言文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
        languageUpdateComplete = config.getString("language-update-complete", "§a语言文件更新完成!");
        languageUpdate(config, lang);
        updateFoundNewVersion = config.getString("update-found-new-version", "§c发现新版本可用! §b当前版本: {0} §d最新版本: {1}");
        updateMajorUpdate = config.getString("update-major-update", "§e(有大更新)");
        updateDownloadLink = config.getString("update-download-link", "§a下载地址: ");
        updateCheckFailure = config.getString("update-check-failure", "§e检查更新失败, 状态码: ");
        updateCheckException = config.getString("update-check-exception", "§4检查更新时发生IO异常.");
        enableMessage = config.getString("enable-message", "§b已启用.");
        disableMessage = config.getString("disable-message", "§c已卸载.");
        StringBuilder helpMsg = new StringBuilder();
        for (String line : config.getStringList("command-help")) {
            if (helpMsg.length() != 0) {
                helpMsg.append('\n');
            }
            helpMsg.append(line);
        }
        if (helpMsg.length() == 0) {
            helpMsg.append("§e===========§bServerMonitor§e===========\n")
                    .append("/ServerMonitor help ---- §a查看指令帮助\n")
                    .append("§e/ServerMonitor reload ---- §a重载插件配置\n")
                    .append("§e/ServerMonitor version ---- §a查看插件版本");
        }
        commandHelp = helpMsg.toString();
        commandReload = config.getString("command-reload", "§a插件配置重载完成.");
        commandUnknown = config.getString("command-unknown", "§c未知的子命令!");
        logPlayerChat = config.getString("log-player-chat", "玩家[{player}]说 : {message}");
        logPlayerCommand = config.getString("log-player-command", "玩家[{player}]({op?})执行命令 : {command}");
        logPlayerCommandOp = config.getString("log-player-command-op", "OP");
        logPlayerCommandNonOp = config.getString("log-player-command-non-op", "非OP");
        logConsoleCommand = config.getString("log-console-command", "控制台[{sender}]执行命令 : {command}");
        logOpChangeOpPlayer = config.getString("log-op-change-op-player", "玩家[{player1}]Opped : {player2}");
        logOpChangeDeopPlayer = config.getString("log-op-change-deop-player", "玩家[{player1}]De-Opped : {player2}");
        logOpChangeOpConsole = config.getString("log-op-change-op-console", "控制台[{sender}]Opped : {player}");
        logOpChangeDeopConsole = config.getString("log-op-change-deop-console", "控制台[{sender}]De-opped : {player}");
        logPlayerGameModeChange = config.getString("log-player-game-mode-change", "玩家[{player}]的游戏模式更改为 : {gamemode}");
        logPlayerJoin = config.getString("log-player-join", "玩家[{player}]({ip}) : 加入服务器");
        logPlayerQuit = config.getString("log-player-quit", "玩家[{player}]({ip}) : 退出服务器");
        logPlayerKick = config.getString("log-player-kick", "玩家[{player}]({ip}) : 被踢出游戏 原因: {reason}");
        messageException = config.getString("message-exception", "异常信息: ");
        messageExceptionOpen = config.getString("message-exception-open", "§4[错误] §5在打开 {file} 时发生IO异常!");
        messageExceptionWrite = config.getString("message-exception-write", "§4[错误] §5在写 {file} 时发生IO异常!");
        messageExceptionClose = config.getString("message-exception-close", "§4[错误] §5在关闭 {file} 时发生IO异常!");
        messageExceptionSave = config.getString("message-exception-save", "§4[错误] §5在保存 {file} 时发生IO异常!");
        messageExceptionZip = config.getString("message-exception-zip", "§4[错误] §5在压缩 {file} 时发生IO异常!");
        messageErrorDelete = config.getString("message-error-delete", "§4[错误] §5删除 {file} 失败!");
    }

    private static void saveDefaultLanguage(File lang, String langPath) {
        if (!lang.exists()) {
            if (plugin.classLoader().getResource(langPath) == null) {
                InputStream in = plugin.getResource("languages/zh_cn.yml");
                if (in != null) {
                    try {
                        OutputStream out = new FileOutputStream(lang);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                        plugin.logMessage("§a语言文件: " + lang.getName() + " 不存在, 已自动创建。");
                    } catch (IOException e) {
                        Util.sendException("§4[错误] §5在保存 " + lang.getName() + " 时发生IO异常!", e.getMessage());
                    }
                } else {
                    plugin.logMessage("§4[错误] 语言文件: " + lang.getName() + " 不存在, 并且在插件内找不到默认语言文件: zh_cn.yml");
                }
            } else {
                plugin.saveResource(langPath, true);
            }
        }
    }

    private static void languageUpdate(YamlConfiguration config, File lang) {
        int latestVersion = 4;
        if (version < latestVersion) {
            plugin.logMessage(replaceArgs(languageVersionOutdated, version, latestVersion));
            switch (version) {
                case 1:
                    config.set("message.zipException", "§4[错误] §5在压缩 {file} 时发生IO异常!");
                    config.set("message.deleteError", "§4[错误] §5删除 {file} 失败!");
                    config.set("message.checkUpdateException", "§4[错误] §c在检查更新时发生IO异常!");
                    config.set("message.checkUpdateError", "§4[错误] §c检查更新失败, 状态码: §b{code}");
                    config.set("message.majorUpdate", "§4重大更新");
                    config.set("message.foundNewVersion", "§c发现新版本：{$version} §b当前版本: {version} 前往查看: {url}");
                case 2:
                    config.set("message.exceptionMessage", "异常信息: ");
                case 3:
                    config.set("log-prefix", "§3[§aServerMonitor§3] ");
                    config.set("message-prefix", "§3[§aServerMonitor§3] §e-> ");
                    config.set("language-version-error", "§c语言文件版本错误: ");
                    config.set("language-version-outdated", "§e当前语言文件版本：§a{0} §c最新版本：§b{1} §6需要更新.");
                    config.set("language-update-complete", "§a语言文件更新完成!");
                    config.set("update-found-new-version", "§c发现新版本可用! §b当前版本: {0} §d最新版本: {1}");
                    config.set("update-major-update", "§e(有大更新)");
                    config.set("update-download-link", "§a下载地址: ");
                    config.set("update-check-failure", "§e检查更新失败, 状态码: ");
                    config.set("update-check-exception", "§4检查更新时发生IO异常.");
                    config.set("enable-message", "§b已启用.");
                    config.set("enabled", null);
                    config.set("disable-message", "§c已卸载.");
                    config.set("disabled", null);
                    config.set("command-help", config.getStringList("helpMsg"));
                    config.set("helpMsg", null);
                    config.set("command-reload", config.getString("reloaded"));
                    config.set("reloaded", null);
                    config.set("command-unknown", config.getString("commandError"));
                    config.set("commandError", null);
                    config.set("log-player-chat", config.getString("log.playerChat"));
                    config.set("log-player-command", config.getString("log.isOP"));
                    config.set("log-player-command-op", config.getString("log.nonOP"));
                    config.set("log-player-command-non-op", config.getString("log.playerCommand"));
                    config.set("log-console-command", config.getString("log.consoleCommand"));
                    config.set("log-op-change-op-player", config.getString("log.opped"));
                    config.set("log-op-change-deop-player", config.getString("log.deOpped"));
                    config.set("log-op-change-op-console", config.getString("log.consoleOpped"));
                    config.set("log-op-change-deop-console", config.getString("log.consoleDeOpped"));
                    config.set("log-player-game-mode-change", config.getString("log.playerGameModeChange"));
                    config.set("log-player-join", config.getString("log.playerJoin"));
                    config.set("log-player-quit", config.getString("log.playerQuit"));
                    config.set("log-player-kick", config.getString("log.playerKick"));
                    config.set("log", null);
                    config.set("message-exception", config.getString("message.exceptionMessage"));
                    config.set("message-exception-open", config.getString("message.openException"));
                    config.set("message-exception-write", config.getString("message.writeException"));
                    config.set("message-exception-close", config.getString("message.closeException"));
                    config.set("message-exception-save", config.getString("message.saveException"));
                    config.set("message-exception-zip", config.getString("message.zipException"));
                    config.set("message-error-delete", config.getString("message.deleteError"));
                    config.set("message", null);
                    break;
                default:
                    plugin.logMessage(languageVersionError + version);
                    return;
            }
            plugin.logMessage(languageUpdateComplete);
            version = latestVersion;
            config.set("version", latestVersion);
            Config.saveConfiguration(config, lang);
        }
    }

    public static String replaceArgs(String msg, Object... args) {
        for (int i = 0; i < args.length; i++) {
            msg = msg.replace("{0}".replace('0', (char) (i + 48)), args[i].toString());
        }
        return msg;
    }

}
