package com.github.myunco.servermonitor.util;

import com.github.myunco.servermonitor.ServerMonitor;
import com.github.myunco.servermonitor.config.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
    public static void warningLog(String str) throws IOException {
        FileWriter fw = new FileWriter(new File(ServerMonitor.plugin.getDataFolder(), "warning.log"), true);
        fw.write(str + Config.lineSeparator);
        fw.close();
    }
}
