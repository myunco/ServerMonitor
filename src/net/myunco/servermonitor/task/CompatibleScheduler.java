package net.myunco.servermonitor.task;

public interface CompatibleScheduler {
    void runTask(Runnable task);
    void runTaskLaterAsynchronously(Runnable task, long delay);
}
