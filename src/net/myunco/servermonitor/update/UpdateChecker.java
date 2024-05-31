package net.myunco.servermonitor.update;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateChecker {
    private static final ServerMonitor plugin = ServerMonitor.getPlugin();
    private static Timer timer;
    private static String downloadLink;

    public static void start() {
        plugin.getServer().getScheduler().runTask(plugin, () -> { // 直接使用Timer不能等到开服完成后再检查更新
            // 什么？你问我为什么要用Timer？我只能告诉你，我忘了我为什么用Timer了。 ———— 2023/12/2
            // 等等，我好像又想起来了，是因为计时准确性。 ———— 2023/12/2
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        CheckResult result = checkVersionUpdate("https://sinacloud.net/myunco/E776DD23/version.txt");
                        if (result.getResultType() == CheckResult.ResultType.SUCCESS) {
                            if (result.hasNewVersion()) {
                                String str = Language.replaceArgs(Language.updateFoundNewVersion, CheckResult.currentVersion, result.getLatestVersion());
                                plugin.logMessage(result.hasMajorUpdate() ? Language.updateMajorUpdate + str : str);
                                // plugin.logMessage(Language.updateDownloadLink + "https://www.mcbbs.net/thread-995756-1-1.html");
                                plugin.logMessage(Language.updateDownloadLink + downloadLink);
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String latestVersion = reader.readLine();
            downloadLink = reader.readLine();
            reader.close();
            conn.disconnect();
            return new CheckResult(latestVersion, code, CheckResult.ResultType.SUCCESS);
        } else {
            return new CheckResult(code, CheckResult.ResultType.FAILURE);
        }
    }

}
