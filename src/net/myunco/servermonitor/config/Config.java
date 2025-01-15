package net.myunco.servermonitor.config;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.util.Util;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static final File configFile = new File(plugin.getDataFolder(), "config.yml");
    public static String language;
    public static String dateFormat;
    public static String lineSeparator;
    public static boolean realTimeSave;
    public static boolean zipOldLog;
    public static int delOldLog;
    public static boolean checkUpdate;
    public static HashMap<String, Boolean> playerChat = new HashMap<>();
    public static HashMap<String, Boolean> playerCommand = new HashMap<>();
    public static HashMap<String, Boolean> playerGameModeChange = new HashMap<>();
    public static boolean opChange;
    public static boolean hidePassword;
    public static boolean joinAndLeave;
    public static boolean keywordsAlertEnable;
    public static List<String> keywordsAlertKeywords;
    public static boolean keywordsAlertCancel;
    public static List<String> keywordsAlertMsg;
    public static boolean keywordsAlertReportAdmin;
    public static boolean keywordsAlertReportConsole;
    public static boolean keywordsAlertSaveToLog;
    public static boolean commandAlertEnable;
    public static List<String> commandAlertPlayerWhitelist;
    public static List<String> commandAlertCommandWhiteList;
    public static boolean commandAlertCancel;
    public static int commandAlertHandleMethod;
    public static HashMap<String, List<String>> commandAlertHandleMethodConfig = new HashMap<>();
    public static boolean dbEnable;
    public static String dbType;
    public static String dbHost;
    public static String dbPort;
    public static String dbUsername;
    public static String dbPassword;
    public static String dbName;
    public static String dbTablePrefix;

    public static void loadConfig() {
        plugin.saveDefaultConfig();
        YamlConfiguration config = updateConfiguration();
        language = config.getString("language", "zh_cn");
        Language.loadLanguage(language);
        dateFormat = config.getString("dateFormat", "yyyy/MM/dd HH:mm:ss");
        Util.setTimeFormat(dateFormat);
        lineSeparator = config.getString("lineSeparator", "Auto");
        assert lineSeparator != null;
        if ("Auto".equalsIgnoreCase(lineSeparator)) {
            lineSeparator = System.lineSeparator();
        } else {
            lineSeparator = lineSeparator.replace("\\r", "\r").replace("\\n", "\n");
        }
        realTimeSave = config.getBoolean("realTimeSave", true);
        zipOldLog = config.getBoolean("zipOldLog", false);
        delOldLog = config.getInt("delOldLog", 0);
        checkUpdate = config.getBoolean("checkUpdate", true);
        playerChat.put("enable", config.getBoolean("playerChat.enable", true));
        playerChat.put("perPlayer", config.getBoolean("playerChat.perPlayer", true));
        playerCommand.put("enable", config.getBoolean("playerCommand.enable", true));
        playerCommand.put("perPlayer", config.getBoolean("playerCommand.perPlayer", true));
        playerCommand.put("consoleCommand", config.getBoolean("playerCommand.consoleCommand", true));
        playerCommand.put("commandBlockCommand", config.getBoolean("playerCommand.commandBlockCommand", true));
        playerGameModeChange.put("enable", config.getBoolean("playerGameModeChange.enable", true));
        playerGameModeChange.put("perPlayer", config.getBoolean("playerGameModeChange.perPlayer", false));
        opChange = config.getBoolean("playerCommand.opChange", true);
        hidePassword = config.getBoolean("playerCommand.hidePassword", false);
        joinAndLeave = config.getBoolean("joinAndLeave", true);
        keywordsAlertEnable = config.getBoolean("keywordsAlert.enable", false);
        commandAlertEnable = config.getBoolean("commandAlert.enable", false);
        if (commandAlertEnable) {
            commandAlertPlayerWhitelist = config.getStringList("commandAlert.whitelist");
            commandAlertCancel = config.getBoolean("commandAlert.cancel", true);
            commandAlertCommandWhiteList = config.getStringList("commandAlert.commandWhiteList");
            commandAlertHandleMethod = config.getInt("commandAlert.handleMethod.method", 0);
            commandAlertHandleMethodConfig.put("broadcast", config.getStringList("commandAlert.handleMethod.broadcast"));
            commandAlertHandleMethodConfig.put("consoleCmd", config.getStringList("commandAlert.handleMethod.consoleCmd"));
            commandAlertHandleMethodConfig.put("playerCmd", config.getStringList("commandAlert.handleMethod.playerCmd"));
            commandAlertHandleMethodConfig.put("playerSendMsg", config.getStringList("commandAlert.handleMethod.playerSendMsg"));
            commandAlertHandleMethodConfig.put("sendMsgToPlayer", config.getStringList("commandAlert.handleMethod.sendMsgToPlayer"));
            commandAlertHandleMethodConfig.put("consoleWarning", config.getStringList("commandAlert.handleMethod.consoleWarning"));
            commandAlertHandleMethodConfig.put("warningLog", config.getStringList("commandAlert.handleMethod.warningLog"));
        }
        if (keywordsAlertEnable) {
            keywordsAlertKeywords = config.getStringList("keywordsAlert.keywords");
            keywordsAlertCancel = config.getBoolean("keywordsAlert.cancel");
            keywordsAlertMsg = config.getStringList("keywordsAlert.alertMsg");
            keywordsAlertReportAdmin = config.getBoolean("keywordsAlert.reportAdmin");
            keywordsAlertReportConsole = config.getBoolean("keywordsAlert.reportConsole");
            keywordsAlertSaveToLog = config.getBoolean("keywordsAlert.saveToLog");
        }
        dbEnable = config.getBoolean("database.enable", false);
        if (dbEnable) {
            dbType = config.getString("database.type", "MySQL");
            dbHost = config.getString("database.host", "localhost");
            dbPort = config.getString("database.port", "3306");
            dbUsername = config.getString("database.username", "root");
            dbPassword = config.getString("database.password", "");
            dbName = config.getString("database.database", "minecraft");
            dbTablePrefix = config.getString("database.tablePrefix", "");
        }
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while((line = reader.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
            } finally {
                reader.close();
            }
            config.loadFromString(builder.toString());
        } catch (Exception e) {
            Util.sendException(e.getClass().getName() + ": " + file.getName(), e.getMessage());
        }
        return config;
    }

    public static YamlConfiguration updateConfiguration() {
        YamlConfiguration config = loadConfiguration(configFile);
        if (!config.contains("keywordsAlert")) { //没有1.4.0版本新加的配置 需要升级
            //更新config会导致注释丢失 为避免这种情况 使用流来追加新内容
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile, true), StandardCharsets.UTF_8)) {
                writer.write("\r\n" +
                        "#关键词警报 当任何非OP玩家执行包含指定关键词的命令时向后台或在线OP发出警报\r\n" +
                        "keywordsAlert:\r\n" +
                        "  #是否启用 true为启用 false为禁用\r\n" +
                        "  #此功能在记录玩家命令启用的情况下才能生效\r\n" +
                        "  enable: false\r\n" +
                        "\r\n" +
                        "  #关键词列表 不区分大小写 按示例格式添加\r\n" +
                        "  keywords:\r\n" +
                        "    - /gamemode\r\n" +
                        "    - /give\r\n" +
                        "    - /op\r\n" +
                        "    - /deop\r\n" +
                        "\r\n" +
                        "  #命令取消执行 true为取消 false为不取消（即使不取消 玩家也未必有权限使用）\r\n" +
                        "  cancel: false\r\n" +
                        "\r\n" +
                        "  #警报信息 支持多行 按格式添加\r\n" +
                        "  alertMsg:\r\n" +
                        "    - '§c玩家§a{player}§c尝试使用命令：§b{command}'\r\n" +
                        "\r\n" +
                        "  #是否通知在线OP true为通知 false为不通知\r\n" +
                        "  reportAdmin: true\r\n" +
                        "\r\n" +
                        "  #是否通知控制台 true为通知 false为不通知\r\n" +
                        "  reportConsole: true\r\n" +
                        "\r\n" +
                        "  #是否保存警告信息到警告日志 true为保存 false为不保存\r\n" +
                        "  saveToLog: true\r\n" +
                        "\r\n" + //顺便把1.5.0版本的新内容也加上
                        "#数据库\r\n" +
                        "database:\r\n" +
                        "  #是否启用数据库存储 true为启用 false为禁用\r\n" +
                        "  enable: false\r\n" +
                        "  #要使用的数据库类型 目前只支持 MySQL\r\n" +
                        "  type: 'MySQL'\r\n" +
                        "  #启用数据库后下面所有项目必须填写 (除了表前缀可空)\r\n" +
                        "  host: 'localhost'\r\n" +
                        "  port: 3306\r\n" +
                        "  username: 'root'\r\n" +
                        "  password: ''\r\n" +
                        "  database: 'minecraft'\r\n" +
                        "  #表前缀 留空表示无前缀 默认表名：chat_log、command_log 以此类推\r\n" +
                        "  tablePrefix: 'sm_'\r\n");
                config = loadConfiguration(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!config.contains("database")) { //没有1.5.0版本新加的配置 需要升级
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile, true), StandardCharsets.UTF_8)) {
                writer.write("\r\n" +
                        "#数据库\r\n" +
                        "database:\r\n" +
                        "  #是否启用数据库存储 true为启用 false为禁用\r\n" +
                        "  enable: false\r\n" +
                        "  #要使用的数据库类型 目前只支持 MySQL\r\n" +
                        "  type: 'MySQL'\r\n" +
                        "  #启用数据库后下面所有项目必须填写 (除了表前缀可空)\r\n" +
                        "  host: 'localhost'\r\n" +
                        "  port: 3306\r\n" +
                        "  username: 'root'\r\n" +
                        "  password: ''\r\n" +
                        "  database: 'minecraft'\r\n" +
                        "  #表前缀\r\n" +
                        "  tablePrefix: 'sm_'\r\n");
                config = loadConfiguration(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    public static void saveConfiguration(YamlConfiguration config, File file) {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8)) {
            writer.write(config.saveToString());
        } catch (IOException e) {
            Util.sendException(Language.messageExceptionSave.replace("{file}", "config.yml"), e.getMessage());
        }
    }

    public static void addToWhitelist(String name) {
        ArrayList<String> text = new ArrayList<>(160);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.add(line);
                if (line.equals("  whitelist:")) {
                    text.add("    - " + name);
                }
            }
        } catch (IOException e) {
            Util.sendException(Language.messageExceptionOpen.replace("{file}", "config.yml"), e.getMessage());
            return;
        }
        writeConfigFile(text);
    }

    public static void removeFromWhitelist(String name) {
        ArrayList<String> text = new ArrayList<>(160);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
            String line;
            String target = "    - " + name.toLowerCase();
            boolean flag = false;
            while ((line = reader.readLine()) != null) {
                if (flag && line.toLowerCase().equals(target)) {
                    continue;
                }
                text.add(line);
                if (line.equals("  whitelist:")) {
                    flag = true;
                } else if (flag && line.endsWith(":")) {
                    flag = false;
                }
            }
        } catch (IOException e) {
            Util.sendException(Language.messageExceptionOpen.replace("{file}", "config.yml"), e.getMessage());
            return;
        }
        writeConfigFile(text);
    }

    private static void writeConfigFile(ArrayList<String> text) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8))) {
            for (String line : text) {
                if (!line.isEmpty()) {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            Util.sendException(Language.messageExceptionWrite.replace("{file}", "config.yml"), e.getMessage());
        }
    }

}
