package net.myunco.servermonitor.update;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.config.Language;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class UpdateNotification implements Listener {
    private int day = LocalDate.now().getDayOfMonth();
    private final ArrayList<UUID> notifiedPlayers = new ArrayList<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (UpdateChecker.isUpdateAvailable && event.getPlayer().hasPermission("servermonitor.admin")) {
            if (day != LocalDate.now().getDayOfMonth()) {
                day = LocalDate.now().getDayOfMonth();
                if (!notifiedPlayers.isEmpty()) {
                    notifiedPlayers.clear();
                }
            }
            if (!notifiedPlayers.contains(event.getPlayer().getUniqueId())) {
                notifiedPlayers.add(event.getPlayer().getUniqueId());
                ServerMonitor.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(ServerMonitor.getPlugin(), () -> {
                    event.getPlayer().sendMessage(Language.messagePrefix + UpdateChecker.newVersion);
                    event.getPlayer().sendMessage(Language.messagePrefix + UpdateChecker.downloadLink);
                }, 60);
            }
        }
    }

}
