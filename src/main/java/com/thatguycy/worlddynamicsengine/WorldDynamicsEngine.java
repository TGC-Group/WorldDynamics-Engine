package com.thatguycy.worlddynamicsengine;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.milkbowl.vault.economy.Economy;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static com.palmergames.bukkit.towny.TownyEconomyHandler.setupEconomy;
import static org.bukkit.Bukkit.getServer;

public final class WorldDynamicsEngine extends JavaPlugin {
    private NationManager nationManager;
    private HumanManager humanManager;

    private OrganizationManager organizationManager;

    private static Economy economy = null;
    private boolean organizationsEnabled;
    private boolean armyEnabled;
    private boolean governmentEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        loadGovernmentTypes();
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        nationManager = new NationManager(this);
        organizationManager = new OrganizationManager(this.getDataFolder());
        humanManager = new HumanManager(this.getDataFolder());
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(humanManager), this);
        if (getServer().getPluginManager().getPlugin("Towny") == null ||
                getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().info("=============================================================");
            getLogger().info(" WorldDynamics Engine failed to start!");
            getLogger().info(" Reason: Vault/Towny not installed or started properly!");
            getLogger().info(" Notes: Open an issue request, or solve it yourself!");
            getLogger().info(" Version: " + this.getDescription().getVersion());
            getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().info("=============================================================");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            Plugin towny = getServer().getPluginManager().getPlugin("Towny");
            String townyVersion = towny.getDescription().getVersion();
            getLogger().info("=============================================================");
            getLogger().info(" WorldDynamics Engine has been successfully enabled!");
            getLogger().info(" Version: " + this.getDescription().getVersion());
            getLogger().info(" Towny Version: " + townyVersion);
            getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
            getLogger().info("=============================================================");
        }
        checkForUpdates();
        this.getCommand("wde").setExecutor(new WDECommandExecutor(nationManager, organizationManager, getEconomy(), this));
        this.getCommand("wde").setTabCompleter(new WDETabCompleter(organizationManager));
        getServer().getPluginManager().registerEvents(new NewDayListener(organizationManager), this);
        startGovernmentAutoSaveTask();
        int pluginId = 20763; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        organizationsEnabled = getConfig().getBoolean("organizations.enabled", true); // Default to true
        armyEnabled = getConfig().getBoolean("army.enabled", true); // Default to true
        governmentEnabled = getConfig().getBoolean("government.enabled", true); // Default to true

    }
    public HumanManager getHumanManager() {
        return humanManager;
    }
    public boolean isOrganizationsEnabled() {
        return organizationsEnabled;
    }

    public boolean isArmyEnabled() {
        return armyEnabled;
    }

    public boolean isGovernmentEnabled() {
        return governmentEnabled;
    }
    private void loadGovernmentTypes() {
        List<String> types = getConfig().getStringList("government_types");
        GovernmentType.loadTypes(new HashSet<>(types));
    }

    private void startGovernmentAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                nationManager.saveNations();
                organizationManager.saveOrganizations();
                humanManager.saveHumans();
            }
        }.runTaskTimer(this, 1200L, 1200L); // 1200L = 60 seconds in ticks
    }

    @Override
    public void onDisable() {
        nationManager.saveNations();
        organizationManager.saveOrganizations();
        humanManager.saveHumans();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }
    private void checkForUpdates() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/thatguycy/WorldDynamics-Engine/master/current.version"); // URL to your version file
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = in.readLine();
            in.close();

            String currentVersion = this.getDescription().getVersion();
            if (!currentVersion.equals(latestVersion)) {
                getLogger().info("=============================================================");
                getLogger().info(" WorldDynamics Engine is out of date!");
                getLogger().info(" Your Version: " + currentVersion);
                getLogger().info(" Our Version: " + latestVersion);
                getLogger().info(" Update: https://github.com/thatguycy/WorldDynamics-Engine");
                getLogger().info(" Craft complex worlds and shape geopolitical adventures!");
                getLogger().info("=============================================================");
            }
        } catch (Exception e) {
            getLogger().warning("Failed to check for updates: " + e.getMessage());
        }
    }

}