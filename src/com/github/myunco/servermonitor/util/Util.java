package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    static SimpleDateFormat sdf;
    static SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
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
        return today.format(d);
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

}
