package com.mogdeveloping.mogac.checks.combat;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import com.mogdeveloping.mogac.utils.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

public class Reach extends Check {

    private static final double MAX_REACH = 3.1;
    private static final double CREATIVE_MAX_REACH = 4.5;

    public Reach(MOGAC plugin) {
        super(plugin, "Reach", CheckType.COMBAT);
    }

    public void checkReach(Player player, Entity target) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000) {
            return;
        }
        
        int ping = ((CraftPlayer)player).getHandle().ping;
        double pingCompensation = ping / 1000.0 * 5.0;
        double maxReach = player.getGameMode() == GameMode.CREATIVE ? CREATIVE_MAX_REACH : MAX_REACH;
        maxReach += pingCompensation;
        
        double distance = MathUtil.getDirectDistance(player, target);
        
        if (distance > maxReach) {
            flag(player, "Distance: " + String.format("%.2f", distance) + " > " + String.format("%.2f", maxReach));
        }
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation());
        }
    }
}