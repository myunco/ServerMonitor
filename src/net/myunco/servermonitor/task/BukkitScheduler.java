package net.myunco.servermonitor.task;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("deprecation")
public class BukkitScheduler implements CompatibleScheduler {
    private final Plugin plugin;
    private final org.bukkit.scheduler.BukkitScheduler scheduler;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }

    @Override
    public void runTask(Runnable task) {
        scheduler.runTask(plugin, task);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable task, long delay) {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }
}
