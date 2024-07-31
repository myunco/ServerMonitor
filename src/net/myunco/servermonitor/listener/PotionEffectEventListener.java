package net.myunco.servermonitor.listener;

import net.myunco.servermonitor.ServerMonitor;
import net.myunco.servermonitor.util.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class PotionEffectEventListener implements Listener {
    public PotionEffectEventListener(ServerMonitor plugin) {
    }

    @EventHandler
    public void playerPotionEffectEvent(EntityPotionEffectEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && event.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK) {
            if (event.getModifiedType().toString().equals("BAD_OMEN")) {
                Entity player = event.getEntity();
                Log.warningLog.write("玩家 " + player.getName() + " 在" + player.getLocation().getBlockX() + "," + player.getLocation().getBlockY() + "," + player.getLocation().getBlockZ() + "处喝下了不祥之兆药水。");
                Log.warningLog.close();
            }
        }
    }
}
