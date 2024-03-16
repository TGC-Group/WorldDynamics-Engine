package com.thatguycy.worlddynamicsengine.residents;

import com.palmergames.bukkit.towny.object.Resident;
import com.thatguycy.worlddynamicsengine.WorldDynamicsEngine;
import com.thatguycy.worlddynamicsengine.util.YamlFileHandler;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Nation;

import java.io.File;
import java.util.UUID;

public class ResidentManager {
    private final WorldDynamicsEngine plugin;
    private final String residentFolder = "residents"; // Folder within your WDE data directory

    public ResidentManager(WorldDynamicsEngine plugin) {
        this.plugin = plugin;
    }

    public void loadResident(Player player) {
        plugin.getLogger().info("Attempting to load resident: " + player.getName());
        UUID uuid = player.getUniqueId();
        YamlFileHandler residentFile = new YamlFileHandler(plugin, residentFolder, uuid + ".yml");
        FileConfiguration residentData = residentFile.getConfig();
    }

    public boolean residentExists(UUID uuid) {
        plugin.getLogger().info("Checking resident existence for UUID: " + uuid);
        File residentFile = new File(plugin.getDataFolder(),  residentFolder + "/" + uuid + ".yml");
        return residentFile.exists();
    }

    public void createResident(Player player) {
        plugin.getLogger().info("Creating new resident: " + player.getName());
        UUID uuid = player.getUniqueId();
        YamlFileHandler residentFile = new YamlFileHandler(plugin, residentFolder, uuid + ".yml");
        FileConfiguration residentData = residentFile.getConfig();

        // Set default data
        residentData.set("UUID", uuid.toString());
        residentData.set("PlayerName", player.getName());
        residentData.set("Nation", null);
        try {
            Resident townyResident = TownyAPI.getInstance().getResident(player);
            if (townyResident.hasNation()) {
                Nation townyNation = townyResident.getNation();
                residentData.set("Nation", townyNation.getName());

                // Role Check (Assuming Towny has a way to get roles)
                if (townyResident.isKing()) {
                    residentData.set("RoleInNation", "King");
                } else {
                    residentData.set("RoleInNation", "Citizen");
                }
            }
        } catch (TownyException e) {
            plugin.getLogger().warning("Error fetching Towny data: " + e.getMessage());
        }

        residentFile.saveData();
    }

    public void updateResident(Player player) { // Change to accept a Player
        UUID uuid = player.getUniqueId();
        YamlFileHandler residentFile = new YamlFileHandler(plugin, "residents", uuid + ".yml");
        FileConfiguration residentData = residentFile.getConfig();

        // Update existing data
        residentData.set("PlayerName", player.getName()); // Update the name

        // Fetch and update Towny-related data
        try {
            Resident townyResident = TownyAPI.getInstance().getResident(player);
            if (townyResident.hasNation()) {
                Nation townyNation = townyResident.getNation();
                residentData.set("Nation", townyNation.getName());

                // Role Check (Assuming Towny has a way to get roles)
                if (townyResident.isKing()) {
                    residentData.set("RoleInNation", "King");
                } else {
                    residentData.set("RoleInNation", "Citizen");
                }
            }
        } catch (TownyException e) {
            plugin.getLogger().warning("Error fetching Towny data: " + e.getMessage());
        }

        residentFile.saveData();
    }
}
