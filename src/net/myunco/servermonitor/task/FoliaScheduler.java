package net.myunco.servermonitor.task;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements CompatibleScheduler {
    private final Plugin plugin;
    private final GlobalRegionScheduler scheduler;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getGlobalRegionScheduler();
    }

    @Override
    public void runTask(Runnable task) {
        scheduler.run(plugin, scheduledTask -> task.run());
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable task, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, tscheduledTask -> task.run(), delay, TimeUnit.SECONDS);
    }
}
