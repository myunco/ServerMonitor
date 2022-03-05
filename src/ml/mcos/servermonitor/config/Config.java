package ml.mcos.servermonitor.config;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.util.Util;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class Config {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static final File configFile = new File(plugin.getDataFolder(), "config.yml");
    private static YamlConfiguration config;
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
    public static boolean joinAndLeave;
    public static List<String> whitelist;
    public static boolean commandAlert;
    public static List<String> commandWhiteList;
    public static boolean cancel;
    public static int handleMethod;
    public static HashMap<String, List<String>> handleMethodConfig = new HashMap<>();

    public static void loadConfig() {
        plugin.saveDefaultConfig();
        config = loadConfiguration(configFile);
        language = config.getString("language", "zh_cn");
        Language.loadLanguage(language);
        dateFormat = config.getString("dateFormat", "yyyy/MM/dd HH:mm:ss");
        Util.setTimeFormat(dateFormat);
        lineSeparator = config.getString("lineSeparator", "Auto");
        if ("Auto".equalsIgnoreCase(lineSeparator)) {
            lineSeparator = System.lineSeparator();
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
        joinAndLeave = config.getBoolean("joinAndLeave", true);
        commandAlert = config.getBoolean("commandAlert.enable", false);
        if (commandAlert) {
            whitelist = config.getStringList("commandAlert.whitelist");
            cancel = config.getBoolean("commandAlert.cancel", true);
            commandWhiteList = config.getStringList("commandAlert.commandWhiteList");
            handleMethod = config.getInt("commandAlert.handleMethod.method", 0);
            handleMethodConfig.put("broadcast", config.getStringList("commandAlert.handleMethod.broadcast"));
            handleMethodConfig.put("consoleCmd", config.getStringList("commandAlert.handleMethod.consoleCmd"));
            handleMethodConfig.put("playerCmd", config.getStringList("commandAlert.handleMethod.playerCmd"));
            handleMethodConfig.put("playerSendMsg", config.getStringList("commandAlert.handleMethod.playerSendMsg"));
            handleMethodConfig.put("sendMsgToPlayer", config.getStringList("commandAlert.handleMethod.sendMsgToPlayer"));
            handleMethodConfig.put("consoleWarning", config.getStringList("commandAlert.handleMethod.consoleWarning"));
            handleMethodConfig.put("warningLog", config.getStringList("commandAlert.handleMethod.warningLog"));
        }
    }

    public static void setValue(String path, Object value) {
        config.set(path, value);
        saveConfiguration(config, configFile);
    }

    public static YamlConfiguration loadConfiguration(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
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
            Util.sendException(Language.messageExceptionSave.replace("{file}", "config.yml"), e.getMessage());
        }
    }

}
