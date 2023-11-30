package ml.mcos.servermonitor.util;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Config;
import ml.mcos.servermonitor.config.Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class Util {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    public static SimpleDateFormat timeFormat;
    public static SimpleDateFormat nameFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        for (String s : Config.commandAlertPlayerWhitelist) {
            if (s.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public static void whitelistAdd(String playerName) {
        if (!isWhitelist(playerName)) {
            Config.commandAlertPlayerWhitelist.add(playerName);
            Config.setValue("commandAlert.whitelist", Config.commandAlertPlayerWhitelist);
        }
    }

    public static void whitelistRemove(String playerName) {
        if (isWhitelist(playerName)) {
            Config.commandAlertPlayerWhitelist.remove(playerName);
            Config.setValue("commandAlert.whitelist", Config.commandAlertPlayerWhitelist);
        }
    }

    public static boolean isWhitelistCommand(String command) {
        for (String s : Config.commandAlertCommandWhiteList) {
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
            plugin.logMessage(Language.messageErrorDelete.replace("{file}", file.getAbsolutePath()));
    }

    public static void zipOldLog() {
        ArrayList<File> files = getFileList(plugin.getDataFolder());
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".log") && !getTextLeft(fileName, ".log").equals(logName)) {
                try {
                    gzipFile(file);
                } catch (IOException e) {
                    sendException(Language.messageExceptionZip.replace("{file}", file.getAbsolutePath()), e.getMessage());
                }
            }
        }
    }

    public static void delOldLog(int days) {
        if (days < 1) {
            return;
        }
        ArrayList<File> files = getFileList(plugin.getDataFolder());
        for (File file : files) {
            String fileTime = getTextLeft(file.getName(), ".log");
            long diff = getDayDiff(logName, fileTime);
            if (diff > days) {
                if (!file.delete()) {
                    plugin.logMessage(Language.messageErrorDelete.replace("{file}", file.getAbsolutePath()));
                }
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
            zipOldLog();
        }
        delOldLog(Config.delOldLog);
    }

    public static void sendException(String msg, String exceptionMsg) {
        plugin.logMessage(msg);
        plugin.logMessage(Language.messageException + exceptionMsg);
    }

}
