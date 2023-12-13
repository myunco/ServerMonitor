package ml.mcos.servermonitor.util;

import java.io.File;

public class SingleLogger extends Logger {
    private final String logName;

    public SingleLogger(File logFolder, String logName) {
        super(logFolder, logName);
        this.logName = logName;
        setDisplayName(logName);
    }

    @Override
    public File getLogFile() {
        return new File(getLogFolder(), logName);
    }
}
