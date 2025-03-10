package com.example.logsplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.InventoryHolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogsPlugin extends JavaPlugin implements Listener {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private File logFile;

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LogsPlugin activé");

        // Create log file
        logFile = new File(getDataFolder(), "logs.txt");
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            getLogger().severe("Impossible de créer le fichier de logs: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("LogsPlugin désactivé");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String playerName = event.getPlayer().getName();
        String blockType = event.getBlock().getType().toString();
        String location = event.getBlock().getLocation().toString();
        String timestamp = LocalDateTime.now().format(formatter);

        logToFile("BlockBreak: " + timestamp + " - " + playerName + " a cassé " + blockType + " à " + location);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof InventoryHolder) {
            InventoryHolder holder = event.getClickedInventory().getHolder();
            if (holder instanceof org.bukkit.entity.Player) {
                String chestOwner = ((org.bukkit.entity.Player) holder).getName();
                String playerName = event.getWhoClicked().getName();
                String item = (event.getCurrentItem() != null) ? event.getCurrentItem().getType().toString() : "null";
                InventoryType inventoryType = event.getClickedInventory().getType();
                String timestamp = LocalDateTime.now().format(formatter);

                logToFile("InventoryClick: " + timestamp + " - " + playerName + " a pris " + item + " du coffre de " + chestOwner + " dans " + inventoryType);
            }
        }
    }

    private void logToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            getLogger().severe("Impossible d'écrire dans le fichier de logs: " + e.getMessage());
        }
    }
}
