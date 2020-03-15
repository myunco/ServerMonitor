package com.github.myunco.servermonitor.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PluginEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerAsyncChatEvent(AsyncPlayerChatEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerGameModeChangeEvent(PlayerGameModeChangeEvent event){
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoinEvent(PlayerJoinEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuitEvent(PlayerQuitEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerKickEvent(PlayerKickEvent event) {
    }
}
