package ml.mcos.servermonitor.update;

import ml.mcos.servermonitor.ServerMonitor;
import ml.mcos.servermonitor.config.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static final String currentVersion = plugin.getDescription().getVersion();
    private static final String[] current = currentVersion.split("\\.");
    private static Timer timer;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> { //直接使用Timer不能等到开服完成后再检查更新
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CheckResult result = checkVersionUpdate("https://sinacloud.net/myunco/E776DD23/version.txt");
                        if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                            if (result.hasNewVersion()) {
                                String str = Language.replaceArgs(Language.updateFoundNewVersion, currentVersion, result.getLatestVersion());
                                plugin.logMessage(result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str);
                                plugin.logMessage(Language.updateDownloadLink + "https://www.mcbbs.net/thread-995756-1-1.html");
                            }
                        } else {
                            plugin.logMessage(Language.updateCheckFailure + result.getResponseCode());
                        }
                    } catch (IOException e) {
                        plugin.logMessage(Language.updateCheckException);
                        e.printStackTrace();
                    }
                }
            }, 10000, 12 * 60 * 60 * 1000); // 开服完成10秒后检查一次，以后每12小时检查一次
        });
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static CheckResult checkVersionUpdate(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        int code = conn.getResponseCode();
        if (code == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine();
            reader.close();
            conn.disconnect();
            if (currentVersion.equals(latestVersion)) {
                return new CheckResult(null, false, code, CheckResult.ResultType.SUCCESS);
            } else {
                String[] latest = latestVersion.split("\\.");
                boolean majorUpdate;
                if (!latest[0].equals(current[0])) {
                    majorUpdate = true;
                } else {
                    majorUpdate = !latest[1].equals(current[1]);
                }
                return new CheckResult(latestVersion, majorUpdate, code, CheckResult.ResultType.SUCCESS);
            }
        } else {
            return new CheckResult(code, CheckResult.ResultType.FAILURE);
        }
    }

}
