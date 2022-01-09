package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.ConfigLoader;
import ml.mcos.servermonitor.config.Language;
import ml.mcos.servermonitor.command.CommandServerMonitor;
import org.bukkit.command.ConsoleCommandSender;

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
    static SimpleDateFormat timeFormat;
    static SimpleDateFormat nameFormat = new SimpleDateFormat("yyyy-MM-dd");
    static ConsoleCommandSender consoleSender = ServerMonitor.consoleSender;
    public static String logName = getToday();

    public static void setTimeFormat(String dateFormat) {
         timeFormat = new SimpleDateFormat(dateFormat);
    }

    public static String getTime() {
        return timeFormat.format(new Date()) + " ";
    }

    public static String getToday() {
        return nameFormat.format(new Date());
    }

    public static String getTextRight(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(index + subStr.length());
    }

    public static String getTextLeft(String str, String subStr) {
        int index = str.indexOf(subStr);
        return index == -1 ? str : str.substring(0, index);
    }

    public static boolean isWhitelist(String playerName) {
        for (String s : Config.whitelist) {
            if (s.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public static void whitelistAdd(String playerName) {
        if (!isWhitelist(playerName)) {
            Config.whitelist.add(playerName);
            ConfigLoader.setValue("commandAlert.whitelist", Config.whitelist);
        }
    }

    public static void whitelistRemove(String playerName) {
        if (isWhitelist(playerName)) {
            Config.whitelist.remove(playerName);
            ConfigLoader.setValue("commandAlert.whitelist", Config.whitelist);
        }
    }

    public static boolean isWhitelistCommand(String command) {
        for (String s : Config.commandWhiteList) {
            if (s.equalsIgnoreCase(command)) {
                return true;
            }
        }
        return false;
    }

    public static void gzipFile(File file) throws IOException {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return;
        }
        GZIPOutputStream gzip = new GZIPOutputStream(new FileOutputStream(file.getAbsolutePath() + ".gz"), 1024);
        int len;
        byte[] buf = new byte[1024];
        while ((len = in.read(buf)) != -1) {
           gzip.write(buf, 0, len);
        }
        in.close();
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
                    sendException(Language.MSG_PREFIX + Language.messageZipException.replace("{file}", file.getAbsolutePath()), e.getMessage());
                }
            }
        }
    }

    public static void delOldLog(int days) {
        if (days < 1) {
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
            return (nameFormat.parse(now).getTime() - nameFormat.parse(before).getTime()) / (1000 * 60 * 60 * 24);
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

    public static void processOldLog() {
        if (Config.zipOldLog) {
            Util.zipOldLog();
        }
        Util.delOldLog(Config.delOldLog);
    }

    public static void checkVersionUpdate() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL("https://sinacloud.net/myunco/E776DD23/version.txt").openConnection();
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine();
            if (!CommandServerMonitor.VERSION.equals(latestVersion)) {
                String[] latest = latestVersion.split("\\."); // version: x.x.x
                String[] current = CommandServerMonitor.VERSION.split("\\.");
                boolean majorUpdate;
                if (!latest[0].equals(current[0])) {
                    majorUpdate = true;
                } else {
                    majorUpdate = !latest[1].equals(current[1]);
                }
                String str = Language.messageFoundNewVersion
                        .replace("{version}", CommandServerMonitor.VERSION)
                        .replace("{$version}", latestVersion)
                        .replace("{url}", "https://www.mcbbs.net/thread-995756-1-1.html");
                consoleSender.sendMessage(Language.MSG_PREFIX + (majorUpdate ? Language.messageMajorUpdate + " - " + str : str));
            }
            reader.close();
            conn.disconnect();
        } else {
            consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageCheckUpdateError.replace("{code}", String.valueOf(code)));
        }
    }

    public static List<String> getTABCompleteList(String[] args, List<String> list) {
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

    public static void sendException(String msg, String exceptionMsg) {
        consoleSender.sendMessage(Language.MSG_PREFIX + msg);
        consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageExceptionMessage + exceptionMsg);
    }
}
