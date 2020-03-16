package com.github.myunco.servermonitor.config;

import java.util.List;
import java.util.HashMap;

public class Config {
    public static String language;
    public static String dateFormat;
    public static String lineSeparator;
    public static Boolean realTimeSave;
    public static HashMap<String, Boolean> playerChat = new HashMap<>();
    public static HashMap<String, Boolean> playerCommand = new HashMap<>();
    public static HashMap<String, Boolean> playerGameModeChange = new HashMap<>();
    public static Boolean opChange;
    public static Boolean joinAndLeave;
    public static List<String> whitelist;
    public static List<String> alertCommandList;
    public static int handleMethod;
    public static HashMap<String, List<String>> handleMethodConfig = new HashMap<>();
}
