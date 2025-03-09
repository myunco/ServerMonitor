package net.myunco.servermonitor.task;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class FoliaScheduler implements CompatibleScheduler {
    private final Plugin plugin;

    public FoliaScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runTask(Runnable task) {
        plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
    }

    @Override
    public void runTaskAsynchronously(Runnable task) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable task, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, tscheduledTask -> task.run(), delay, TimeUnit.SECONDS);
    }

}
