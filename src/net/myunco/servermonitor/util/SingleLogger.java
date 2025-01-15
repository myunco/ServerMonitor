package net.myunco.servermonitor.util;

import net.myunco.servermonitor.database.DataSource;

import java.io.File;

public class SingleLogger extends Logger {
    private final String logName;

    public SingleLogger(File logFolder, String logName) {
        super(logFolder, logName);
        this.logName = logName;
        setDisplayName(logName);
    }

    public SingleLogger(File logFolder, String logName, DataSource dataSource)  {
        super(logFolder, logName, dataSource);
        this.logName = logName;
        setDisplayName(logName);
    }

    @Override
    public File getLogFile() {
        return new File(getLogFolder(), logName);
    }
}
