package ml.mcos.servermonitor.config;

import com.google.common.base.Charsets;
import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {
    static ServerMonitor plugin = ServerMonitor.plugin;
    static File configFile = new File(plugin.getDataFolder(), "config.yml");
    static YamlConfiguration config;

    public static void load() {
        plugin.saveDefaultConfig();
        config = loadConfiguration(configFile);
        Config.language = config.getString("language", "zh_cn");
        loadLanguage(Config.language);
        Config.dateFormat = config.getString("dateFormat", "yyyy/MM/dd HH:mm:ss");
        Util.setTimeFormat(Config.dateFormat);
        Config.lineSeparator = config.getString("lineSeparator", "Auto");
        if ("auto".equalsIgnoreCase(Config.lineSeparator)) {
            Config.lineSeparator = System.lineSeparator();
        }
        Config.realTimeSave = config.getBoolean("realTimeSave", true);

        Config.zipOldLog = config.getBoolean("zipOldLog", false);
        Config.delOldLog = config.getInt("delOldLog", 0);
        Config.checkUpdate = config.getBoolean("checkUpdate", true);

        Config.playerChat.put("enable", config.getBoolean("playerChat.enable", true));
        Config.playerChat.put("perPlayer", config.getBoolean("playerChat.perPlayer", true));
        Config.playerCommand.put("enable", config.getBoolean("playerCommand.enable", true));
        Config.playerCommand.put("perPlayer", config.getBoolean("playerCommand.perPlayer", true));
        Config.playerCommand.put("consoleCommand", config.getBoolean("playerCommand.consoleCommand", true));
        Config.playerCommand.put("commandBlockCommand", config.getBoolean("playerCommand.commandBlockCommand", true));
        Config.playerGameModeChange.put("enable", config.getBoolean("playerGameModeChange.enable", true));
        Config.playerGameModeChange.put("perPlayer", config.getBoolean("playerGameModeChange.perPlayer", false));
        Config.opChange = config.getBoolean("playerCommand.opChange", true);
        Config.joinAndLeave = config.getBoolean("joinAndLeave", true);
        Config.commandAlert = config.getBoolean("commandAlert.enable", true);
        if (Config.commandAlert) {
            Config.whitelist = config.getStringList("commandAlert.whitelist");
            Config.cancel = config.getBoolean("commandAlert.cancel", true);
            Config.commandWhiteList = config.getStringList("commandAlert.commandWhiteList");
            ConfigurationSection section = config.getConfigurationSection("commandAlert.handleMethod");
            if (section != null) {
                for (String value : section.getKeys(false)) {
                    if (value.equals("method")) {
                        Config.handleMethod = section.getInt("method");
                        continue;
                    }
                    Config.handleMethodConfig.put(value, section.getStringList(value));
                }
            }
        }
        plugin.enable();
    }

    public static void reload() {
        plugin.disable();
        load();
    }

    public static void loadLanguage(String language) {
        String langPath = "languages/" + language + ".yml";
        File file = new File(plugin.getDataFolder(), langPath);
        if (!file.exists()) {
            if (plugin.getResource(langPath) == null) {
                InputStream in = plugin.getResource("languages/zh_cn.yml");
                if (in != null) {
                    try {
                        OutputStream out = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                        out.close();
                        in.close();
                    } catch (IOException e) {
                        Util.sendException("§4[错误] §5在保存 " + language + ".yml 时发生IO异常!", e.getMessage());
                    }
                    plugin.getLogger().info("语言文件: " + file.getPath() + " 不存在,已自动创建.");
                } else {
                    plugin.getLogger().severe("错误: 语言文件: " + language + ".yml 不存在,并且在插件本体内找不到默认语言文件: zh_cn.yml");
                }
            } else {
                plugin.saveResource(langPath, false);
            }
        }
        YamlConfiguration lang = loadConfiguration(file);
        Language.version = lang.getInt("version");
        Language.messageSaveException = lang.getString("message.saveException");
        Language.messageLangUpdate = lang.getString("message.langUpdate");
        Language.messageLangUpdated = lang.getString("message.langUpdated");
        if (languageUpdate(lang)) {
            try {
                lang.save(file);
                ServerMonitor.consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageLangUpdated);
            } catch (IOException e) {
                Util.sendException(Language.messageSaveException.replace("{file}", language + ".yml"), e.getMessage());
            }
        }
        Language.enabled = lang.getString("enabled");
        Language.disabled = lang.getString("disabled");
        StringBuilder helpMsg = new StringBuilder();
        lang.getStringList("helpMsg").forEach(value -> helpMsg.append(value).append("\n"));
        Language.helpMsg = helpMsg.toString();
        Language.reloaded = lang.getString("reloaded");
        Language.commandError = lang.getString("commandError");
        Language.logPlayerChat = lang.getString("log.playerChat");
        Language.logIsOp = lang.getString("log.isOP");
        Language.logNonOp = lang.getString("log.nonOP");
        Language.logPlayerCommand = lang.getString("log.playerCommand");
        Language.logConsoleCommand = lang.getString("log.consoleCommand");
        Language.logOpped = lang.getString("log.opped");
        Language.logDeOpped = lang.getString("log.deOpped");
        Language.logConsoleOpped = lang.getString("log.consoleOpped");
        Language.logConsoleDeOpped = lang.getString("log.consoleDeOpped");
        Language.logPlayerGameModeChange = lang.getString("log.playerGameModeChange");
        Language.logPlayerJoin = lang.getString("log.playerJoin");
        Language.logPlayerQuit = lang.getString("log.playerQuit");
        Language.logPlayerKick = lang.getString("log.playerKick");
        Language.messageOpenException = lang.getString("message.openException");
        Language.messageWriteException = lang.getString("message.writeException");
        Language.messageCloseException = lang.getString("message.closeException");
        Language.messageConfigError = lang.getString("message.configError");
        Language.messageZipException = lang.getString("message.zipException");
        Language.messageDeleteError = lang.getString("message.deleteError");
        Language.messageCheckUpdateException = lang.getString("message.checkUpdateException");
        Language.messageCheckUpdateError = lang.getString("message.checkUpdateError");
        Language.messageMajorUpdate = lang.getString("message.majorUpdate");
        Language.messageFoundNewVersion = lang.getString("message.foundNewVersion");
        Language.messageExceptionMessage = lang.getString("message.exceptionMessage", "异常信息: ");
    }

    public static boolean languageUpdate(YamlConfiguration lang) {
        int currentVersion = 3;
        if (Language.version < currentVersion) {
            ServerMonitor.consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageLangUpdate.replace("{version}", String.valueOf(Language.version)).replace("{$version}", String.valueOf(currentVersion)));
            switch (Language.version) {
                case 1: //如果版本为1 则添加相对于版本2缺少的内容
                    lang.set("message.zipException", "§4[错误] §5在压缩 {file} 时发生IO异常!");
                    lang.set("message.deleteError", "§4[错误] §5删除 {file} 失败!");
                    lang.set("message.checkUpdateException", "§4[错误] §c在检查更新时发生IO异常!");
                    lang.set("message.checkUpdateError", "§4[错误] §c检查更新失败, 状态码: §b{code}");
                    lang.set("message.majorUpdate", "§4重大更新");
                    lang.set("message.foundNewVersion", "§c发现新版本：{$version} §b当前版本: {version} 前往查看: {url}");
                case 2: //如果版本为2 则添加相对于版本3缺少的内容
                    lang.set("message.exceptionMessage", "异常信息: ");
                    break;
                default:
                    plugin.getLogger().warning("语言文件版本错误: " + Language.version);
            }
            Language.version = currentVersion;
            lang.set("version", Language.version);
            return true;
        }
        return false;
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    builder.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
            config.loadFromString(builder.toString());
        } catch (FileNotFoundException e) {
            Util.sendException("FileNotFoundException: " + file.getName(), e.getMessage());
        } catch (IOException e) {
            Util.sendException("IOException: " + file.getName(), e.getMessage());
        } catch (InvalidConfigurationException e) {
            Util.sendException("InvalidConfigurationException: " + file.getName(), e.getMessage());
        }
        return config;
    }

    public static void saveConfiguration(YamlConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (IOException e) {
            Util.sendException(Language.messageSaveException.replace("{file}", "config.yml"), e.getMessage());
        }
    }

    public static void setValue(String path, Object value) {
        config.set(path, value);
        saveConfiguration(config, configFile);
    }
}
