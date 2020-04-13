package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;
import com.github.myunco.servermonitor.config.Language;

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
    static SimpleDateFormat sdf;
    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
        //return Config.whitelist.contains(playerName);
        for (String s : Config.whitelist) {
            if (s.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCommandWhiteList(String command) {
        //return Config.commandWhiteList.contains(command);
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
            ServerMonitor.consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageDeleteError);
    }

    public static void zipOldLog() {
        ArrayList<File> files = getFileList(ServerMonitor.plugin.getDataFolder());
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".log") && !getTextLeft(fileName, ".log").equals(logName)) {
                try {
                    gzipFile(file);
                } catch (IOException e) {
                    Log.sendException(Language.MSG_PREFIX + Language.messageZipException, e.getMessage());
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
                    ServerMonitor.consoleSender.sendMessage(Language.MSG_PREFIX + Language.messageDeleteError);
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
}
