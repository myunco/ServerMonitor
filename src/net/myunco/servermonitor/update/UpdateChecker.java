package net.myunco.servermonitor.update;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Language;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static Timer timer;
    static boolean isUpdateAvailable;
    static String newVersion;
    static String downloadLink;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    CheckResult result = new CheckResult("https://myunco.sinacloud.net/E776DD23/ServerMonitor.txt", plugin.getDescription().getVersion());
                    if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                        if (result.hasNewVersion()) {
                            isUpdateAvailable = true;
                            String str = Language.replaceArgs(Language.updateFoundNewVersion, result.getCurrentVersion(), result.getLatestVersion());
                            newVersion = result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str;
                            downloadLink = Language.updateDownloadLink + result.getDownloadLink();
                            plugin.logMessage(newVersion);
                            plugin.logMessage(downloadLink);
                            plugin.logMessage(result.getUpdateInfo());
                        } else {
                            isUpdateAvailable = false;
                        }
                    } else {
                        plugin.logMessage(Language.updateCheckFailure + result.getErrorMessage());
                    }
                }
            }, 5000, 12 * 60 * 60 * 1000); // 开服完成5秒后检查一次，以后每12小时检查一次
        });
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
