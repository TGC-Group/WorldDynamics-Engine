package com.thatguycy.worlddynamicsengine;

import com.thatguycy.worlddynamicsengine.listeners.PlayerListener;
import com.thatguycy.worlddynamicsengine.nations.NationManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.palmergames.bukkit.towny.TownyAPI; // Import the core API class
import com.palmergames.bukkit.towny.object.Nation; // To work with Towny nations
import java.util.List;

public class WorldDynamicsEngine extends JavaPlugin {
    private static WorldDynamicsEngine instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("============================================");
        getLogger().info("WorldDynamics Engine v0.3.0 has been enabled!");
        getLogger().info("============================================");

        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            setupTownyIntegration();
        } else {
            getLogger().warning("Towny not found! Disabling Towny integration.");
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void setupTownyIntegration() {
        TownyAPI townyAPI = TownyAPI.getInstance();

        // Fetch existing Towny nations
        List<Nation> townyNations = townyAPI.getNations();

        // Iterate and create WDE counterparts
        for (Nation townyNation : townyNations) {
            createWDENationFromTowny(townyNation);
        }
    }

    private void createWDENationFromTowny(Nation townyNation) {
        String nationName = townyNation.getName();

        NationManager nationManager = new NationManager(this); // Pass plugin instance
        nationManager.createNationFile(nationName);
    }

    @Override
    public void onDisable() {
        getLogger().info("============================================");
        getLogger().info("WorldDynamics Engine v0.3.0 has been disabled!");
        getLogger().info("============================================");
    }


    public static WorldDynamicsEngine getInstance() {
        return instance;
    }
}