package com.thatguycy.worlddynamicsengine.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class YamlFileHandler {

    private final JavaPlugin plugin;
    private final String fileName;
    private final File folder;
    private File file;
    private FileConfiguration config;

    public YamlFileHandler(JavaPlugin plugin, String folderName, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.folder = new File(plugin.getDataFolder(), folderName);
        createFiles();
    }

    private void createFiles() {
        if (!folder.exists()) {
            folder.mkdir();
        }
        file = new File(folder, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveData() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
