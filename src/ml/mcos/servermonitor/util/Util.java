package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.ConfigLoader;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.command.CommandServerMonitor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class Util {
    static SimpleDateFormat sdf;
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    static ConsoleCommandSender consoleSender = ServerMonitor.consoleSender;
    public static String logName = getToday();

    public static void setSdf(String dateFormat) {
         sdf = new SimpleDateFormat(dateFormat);
    }

    public static String getTime() {
        Date d = new Date();
        return sdf.format(d) + " ";
    }

    public static String getToday() {
        Date d = new Date();
        return format.format(d);
    }

    public static String getTextRight(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(index + subStr.length());
    }

    public static String getTextLeft(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(0, index);
    }

    public static boolean isWhiteList(String playerName) {
        for (String s : Config.whitelist) {
            if (s.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addWhiteList(String playerName) {
        if (isWhiteList(playerName)) {
            return true;
        }
        Config.whitelist.add(playerName);
         return setConfigValue("commandAlert.whitelist", Config.whitelist);
    }

    public static boolean delWhiteList(String playerName) {
        if (!isWhiteList(playerName)) {
            return true;
        }
        return Config.whitelist.remove(playerName) && setConfigValue("commandAlert.whitelist", Config.whitelist);
    }

    public static boolean setConfigValue(String path, Object value) {
        ServerMonitor.plugin.saveDefaultConfig();
        File file = new File(ServerMonitor.plugin.getDataFolder(), "/config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, value);
        return ConfigLoader.save(config, file);
    }

    public static boolean isCommandWhiteList(String command) {
        for (String s : Config.commandWhiteList) {
            if (s.equalsIgnoreCase(command)) {
                return true;
            }
        }
        return false;
    }

    public static void gzipFile(File file) throws IOException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return;
        }
        GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(file.getAbsolutePath() + ".gz"), 1024);
        int len;
        byte[] buf = new byte[1024];
        while ((len = fis.read(buf)) != -1) {
           gzip.write(buf, 0, len);
        }
        fis.close();
        gzip.close();
        if (!file.delete())
            consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageDeleteError.replace("{file}", file.getAbsolutePath()));
    }

    public static void zipOldLog() {
        ArrayList<File> files = getFileList(ServerMonitor.plugin.getDataFolder());
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".log") && !getTextLeft(fileName, ".log").equals(logName)) {
                try {
                    gzipFile(file);
                } catch (IOException e) {
                    Log.sendException(Language.MSG_PREFIX + Language.messageZipException.replace("{file}", file.getAbsolutePath()), e.getMessage());
                }
            }
        }
    }

    public static void delOldLog(int days) {
        if (days <= 0) {
            return;
        }
        ArrayList<File> files = getFileList(ServerMonitor.plugin.getDataFolder());
        for (File file : files) {
            String fileTime = getTextLeft(file.getName(), ".log");
            long diff = getDayDiff(logName, fileTime);
            if (diff > days) {
                if (!file.delete())
                    consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageDeleteError.replace("{file}", file.getAbsolutePath()));
            }
        }
    }

    public static long getDayDiff(String now, String before) {
        try {
            return (format.parse(now).getTime() - format.parse(before).getTime()) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            return 0;
        }
    }

    public static ArrayList<File> getFileList(File dir) {
        File[] files = dir.listFiles(pathname -> {
            if (pathname.isDirectory()) {
                return true;
            }
            String name = pathname.getName();
            return (name.length() == 14 || name.length() == 17) && (name.endsWith(".log") || name.endsWith(".log.gz"));
        });
        ArrayList<File> result = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getFileList(file));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }

    public static void logInit() {
        if (Config.zipOldLog) {
            Util.zipOldLog();
        }
        Util.delOldLog(Config.delOldLog);
    }

    public static void checkVersionUpdate() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL("https://sinacloud.net/myunco/E776DD23/version.txt").openConnection();
        int code = conn.getResponseCode();
        String[] ret = new String[2];
        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            ret[0] = reader.readLine(); //读取最新版本
            ret[1] = reader.readLine(); //读取是否重大更新
            if (!CommandServerMonitor.VERSION.equals(ret[0])) {
                String str = Language.messageFoundNewVersion
                        .replace("{version}", CommandServerMonitor.VERSION)
                        .replace("{$version}", ret[0])
                        .replace("{url}", "https://www.mcbbs.net/thread-995756-1-1.html");
                consoleSender.sendMessage(Language.MSG_PREFIX + ("true".equals(ret[1]) ? Language.messageMajorUpdate + " - " + str : str));
            }
            reader.close();
        } else {
            consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageCheckUpdateError.replace("{code}", String.valueOf(code)));
        }
    }

    public static List<String> getCompleteList(String[] args, List<String> list) {
        List<String> ret = new ArrayList<>();
        if (list == null) {
            return ret;
        } else if (list.isEmpty()) {
            return null;
        } else if (args[args.length - 1].isEmpty()) {
            return list;
        }
        String arg = args[args.length - 1].toLowerCase();
        for (String value : list) {
            if (value.startsWith(arg)) {
                ret.add(value);
            }
        }
        return ret;
    }

}
