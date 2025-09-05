package com.mogdeveloping.mogac.checks;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.entity.Player;

public abstract class Check {

    protected final MOGAC plugin;
    protected final String name;
    protected final CheckType type;
    protected boolean enabled = true;
    protected int maxViolations = 10;
    protected String punishCommand = "kick %player% [MOG-AC] Unfair advantage";

    public Check(MOGAC plugin, String name, CheckType type) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
    }

    public abstract void onViolation(Player player, double vl, String details);

    protected void flag(Player player, String details) {
        if (!isEnabled() || player.hasPermission("mogac.bypass")) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.incrementViolations(this.getClass());
        
        int violations = data.getViolations(this.getClass());
        
        plugin.alert(player.getName() + " failed " + name + " check. VL: " + violations + " (" + details + ")");
        
        if (violations >= maxViolations) {
            punish(player);
            data.resetViolations(this.getClass());
        }
        
        onViolation(player, violations, details);
    }

    protected void punish(Player player) {
        String command = punishCommand.replace("%player%", player.getName());
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
    }

    public String getName() {
        return name;
    }

    public CheckType getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaxViolations() {
        return maxViolations;
    }

    public void setMaxViolations(int maxViolations) {
        this.maxViolations = maxViolations;
    }

    public String getPunishCommand() {
        return punishCommand;
    }

    public void setPunishCommand(String punishCommand) {
        this.punishCommand = punishCommand;
    }
}