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

public class Spider extends Check {

    public Spider(MOGAC plugin) {
        super(plugin, "Spider", CheckType.MOVEMENT);
    }

    public void checkSpider(Player player, Location from, Location to) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR ||
            player.isFlying() || player.getAllowFlight()) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000 || 
            System.currentTimeMillis() - data.getLastVelocityTime() < 1000) {
            return;
        }
        
        double deltaY = to.getY() - from.getY();
        
        if (deltaY > 0 && !hasClimbableBlock(player)) {
            Block blockAt = to.getBlock();
            Block blockBelow = to.clone().subtract(0, 0.1, 0).getBlock();
            
            if (!data.isOnGround() && !blockAt.getType().isSolid() && !blockBelow.getType().isSolid()) {
                Block blockNorth = to.clone().add(0, 0, -0.3).getBlock();
                Block blockSouth = to.clone().add(0, 0, 0.3).getBlock();
                Block blockEast = to.clone().add(0.3, 0, 0).getBlock();
                Block blockWest = to.clone().add(-0.3, 0, 0).getBlock();
                
                if ((blockNorth.getType().isSolid() || blockSouth.getType().isSolid() || 
                     blockEast.getType().isSolid() || blockWest.getType().isSolid()) && 
                    !canJumpFromNearby(to)) {
                    flag(player, "Climbing: " + String.format("%.2f", deltaY));
                }
            }
        }
    }
    
    private boolean hasClimbableBlock(Player player) {
        Location loc = player.getLocation();
        Block block = loc.getBlock();
        
        return block.getType() == Material.LADDER || block.getType() == Material.VINE;
    }
    
    private boolean canJumpFromNearby(Location location) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                
                Block block = location.clone().add(x, -1, z).getBlock();
                if (block.getType().isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation().subtract(0, 0.1, 0));
        }
    }
}