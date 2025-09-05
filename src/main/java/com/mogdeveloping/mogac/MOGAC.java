package com.mogdeveloping.mogac;

import com.mogdeveloping.mogac.checks.CheckManager;
import com.mogdeveloping.mogac.commands.MOGACCommand;
import com.mogdeveloping.mogac.data.PlayerDataManager;
import com.mogdeveloping.mogac.listeners.PacketListener;
import com.mogdeveloping.mogac.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MOGAC extends JavaPlugin {

    private static MOGAC instance;
    private PlayerDataManager playerDataManager;
    private CheckManager checkManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        playerDataManager = new PlayerDataManager();
        checkManager = new CheckManager(this);

        registerListeners();
        registerCommands();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MOG-AC] MOG-ANTICHEAT_V1.1 enabled.");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[MOG-AC] Credits: MOG-Developing");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MOG-AC] MOG-ANTICHEAT_V1.1 disabled.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        new PacketListener(this).register();
    }

    private void registerCommands() {
        getCommand("mogac").setExecutor(new MOGACCommand(this));
    }

    public static MOGAC getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public CheckManager getCheckManager() {
        return checkManager;
    }

    public void alert(String message) {
        String prefix = getConfig().getString("alerts-prefix", "[MOG-ANTICHEAT_V1.1]");
        String formattedMessage = ChatColor.RED + prefix + " " + ChatColor.GRAY + message;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("mogac.alerts")) {
                player.sendMessage(formattedMessage);
            }
        }

        Bukkit.getConsoleSender().sendMessage(formattedMessage);
    }
}