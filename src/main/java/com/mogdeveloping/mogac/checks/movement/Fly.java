package com.mogdeveloping.mogac.checks.movement;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Fly extends Check {

    private final double verticalThreshold;
    private final int airTicksThreshold;
    private final long exemptVelocityMs;

    public Fly(MOGAC plugin) {
        super(plugin, "Fly", CheckType.MOVEMENT);
        this.verticalThreshold = plugin.getConfig().getDouble("checks.Fly.vertical-threshold", 0.5);
        this.airTicksThreshold = plugin.getConfig().getInt("checks.Fly.air-ticks-threshold", 14);
        this.exemptVelocityMs = plugin.getConfig().getLong("checks.Fly.exempt-velocity-ms", 1500L);
    }

    public void checkFly(Player player, Location from, Location to) {
        if (!enabled)
            return;
        if (player.getGameMode() == GameMode.CREATIVE)
            return;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data.isTeleporting())
            return;
        if (System.currentTimeMillis() - data.getLastVelocityTime() < exemptVelocityMs)
            return;
        double dy = to.getY() - from.getY();
        boolean onGround = player.isOnGround();
        boolean nearGround = isNearGround(to);
        if (!onGround && !nearGround && dy > -0.05 && dy < verticalThreshold) {
            int airTicks = player.getRemainingAir();
            if (airTicks > airTicksThreshold) {
                flag(player, "airTicks=" + airTicks + ", dy=" + String.format("%.3f", dy));
            }
        }
    }

    private boolean isNearGround(Location loc) {
        Block below = loc.clone().subtract(0, 0.01, 0).getBlock();
        if (below.getType() != Material.AIR)
            return true;
        for (int y = 1; y <= 2; y++) {
            Block b = loc.clone().subtract(0, y, 0).getBlock();
            if (b.getType() != Material.AIR)
                return true;
        }
        return false;
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(plugin.getPlayerDataManager().getPlayerData(player).getLastOnGroundLocation());
        }
    }
}
