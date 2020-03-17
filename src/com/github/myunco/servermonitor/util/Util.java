package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    static SimpleDateFormat sdf = new SimpleDateFormat(Config.dateFormat);

    public static String getTime() {
        Date d = new Date();
        return sdf.format(d);
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
        return Config.whitelist.contains(playerName);
    }

    public static boolean isCommandWhiteList(String command) {
        return Config.commandWhiteList.contains(command);
    }

}
