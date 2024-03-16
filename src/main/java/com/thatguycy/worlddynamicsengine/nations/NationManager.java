package com.thatguycy.worlddynamicsengine.nations; // Update to your nations package

import com.thatguycy.worlddynamicsengine.WorldDynamicsEngine;
import com.thatguycy.worlddynamicsengine.util.YamlFileHandler;

public class NationManager {

    private final WorldDynamicsEngine plugin;

    public NationManager(WorldDynamicsEngine plugin) {
        this.plugin = plugin;
    }

    public void createNationFile(String nationName) {
        YamlFileHandler nationFile = new YamlFileHandler(plugin, "nations", nationName + ".yml");

        // Set default data for the nation:
        nationFile.getConfig().set("name", nationName);
        nationFile.getConfig().set("leader", null);
        nationFile.saveData();
    }
}
