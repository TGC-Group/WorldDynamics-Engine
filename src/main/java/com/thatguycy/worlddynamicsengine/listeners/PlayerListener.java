package com.thatguycy.worlddynamicsengine.listeners;

import com.thatguycy.worlddynamicsengine.WorldDynamicsEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.thatguycy.worlddynamicsengine.residents.ResidentManager;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final WorldDynamicsEngine plugin; // Store a reference
    private final ResidentManager residentManager;

    public PlayerListener(WorldDynamicsEngine plugin) {
        this.plugin = plugin;
        this.residentManager = new ResidentManager(plugin);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        plugin.getLogger().info("Player joined: " + player.getName() + " (UUID: " + uuid + ")");

        if (residentManager.residentExists(uuid)) {
            residentManager.updateResident(player);
        } else {
            plugin.getLogger().info("Resident not found, creating new resident.");
            residentManager.createResident(player);
        }
    }
}
