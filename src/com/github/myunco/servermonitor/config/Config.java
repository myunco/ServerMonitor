package com.github.myunco.servermonitor.config;

import java.util.HashMap;
import java.util.List;

public class Config {
    public static String language;
    public static String dateFormat;
    public static String lineSeparator;
    public static boolean realTimeSave;
    public static boolean zipOldLog;
    public static int delOldLog;
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
}
