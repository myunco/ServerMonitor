package net.myunco.servermonitor.util;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Config;
import net.myunco.servermonitor.config.Language;
import net.myunco.servermonitor.database.DataSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Logger {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private final File logFolder;
    private BufferedWriter logWriter;
    private String displayName;
    private final DataSource dataSource;

    public Logger(File logFolder, String identifier) {
        this(logFolder, identifier, null);
    }

    public Logger(File logFolder, String identifier, DataSource dataSource) {
        this.logFolder = logFolder;
        this.displayName = identifier;
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    protected File getLogFolder() {
        return logFolder;
    }

    protected File getLogFile() {
        return new File(logFolder, Util.logName);
    }

    public void open() {
        File file = getLogFile();
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            plugin.logMessage(Language.messageErrorCreate.replace("{file}", file.getParentFile().getAbsolutePath()));
            return;
        }
        try {
            logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8), 1024);
        } catch (FileNotFoundException e) {
            Util.sendException(Language.messageExceptionOpen.replace("{file}", displayName), e.getMessage());
        }
    }

    public void write(String msg) {
        if (logWriter == null) {
            open();
        }
        if (logWriter != null) {
            try {
                logWriter.write(msg + Config.lineSeparator);
                if (Config.realTimeSave) {
                    logWriter.flush();
                }
            } catch (IOException e) {
                Util.sendException(Language.messageExceptionWrite.replace("{file}", displayName), e.getMessage());
            }
        }
    }

    public void flush() {
        if (logWriter != null) {
            try {
                logWriter.flush();
            } catch (IOException e) {
                Util.sendException(Language.messageExceptionWrite.replace("{file}", displayName), e.getMessage());
            }
        }
    }

    public void close() {
        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                Util.sendException(Language.messageExceptionClose.replace("{file}", displayName), e.getMessage());
            }
            logWriter = null;
        }
    }

}
