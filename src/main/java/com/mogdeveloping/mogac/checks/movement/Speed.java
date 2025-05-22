package com.mogdeveloping.mogac.checks.movement;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Speed extends Check {

    private static final double MAX_XZ_SPEED = 0.70;
    private static final double MAX_Y_SPEED = 0.68;

    public Speed(MOGAC plugin) {
        super(plugin, "Speed", CheckType.MOVEMENT);
    }

    public void checkSpeed(Player player, Location from, Location to) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR ||
            player.isFlying() || player.getAllowFlight()) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000 || 
            System.currentTimeMillis() - data.getLastVelocityTime() < 1000) {
            return;
        }
        
        double deltaX = to.getX() - from.getX();
        double deltaY = to.getY() - from.getY();
        double deltaZ = to.getZ() - from.getZ();
        
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        int speedLevel = 0;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType() == PotionEffectType.SPEED) {
                speedLevel = effect.getAmplifier() + 1;
                break;
            }
        }
                         
        double maxSpeed = MAX_XZ_SPEED;
        
        if (speedLevel > 0) {
            maxSpeed += speedLevel * 0.06;
        }
        
        if (player.isSprinting()) {
            maxSpeed *= 1.6;
        }
        
        if (horizontalDistance > maxSpeed) {
            flag(player, "Horizontal: " + String.format("%.2f", horizontalDistance) + " > " + String.format("%.2f", maxSpeed));
        }
        
        if (!data.isOnGround() && !data.wasOnGround() && deltaY > 0 && deltaY > MAX_Y_SPEED) {
            flag(player, "Vertical: " + String.format("%.2f", deltaY) + " > " + String.format("%.2f", MAX_Y_SPEED));
        }
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation());
        }
    }
}