package com.mogdeveloping.mogac.checks.world;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Destroyer extends Check {

    private final Map<UUID, Integer> blockBreakCount = new HashMap<>();
    private final Map<UUID, Long> blockBreakTime = new HashMap<>();

    private static final int MAX_BREAK_PER_SECOND = 15;

    public Destroyer(MOGAC plugin) {
        super(plugin, "Destroyer", CheckType.WORLD);
    }

    public void checkDestroyer(Player player, BlockBreakEvent event) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        
        if (!blockBreakTime.containsKey(uuid)) {
            blockBreakTime.put(uuid, now);
            blockBreakCount.put(uuid, 1);
            return;
        }
        
        long lastTime = blockBreakTime.get(uuid);
        int count = blockBreakCount.get(uuid);
        
        if (now - lastTime < 1000) {
            count++;
        } else {
            blockBreakTime.put(uuid, now);
            count = 1;
        }
        
        if (count > MAX_BREAK_PER_SECOND) {
            flag(player, "Breaking blocks too fast: " + count + " > " + MAX_BREAK_PER_SECOND);
        }
        
        blockBreakCount.put(uuid, count);
        checkFastBreak(player, event.getBlock());
    }
    
    private void checkFastBreak(Player player, Block block) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        long now = System.currentTimeMillis();
        long lastBreak = data.getLastBlockBreakTime();
        
        if (lastBreak > 0 && now - lastBreak < 100) {
            flag(player, "Fast break: " + (now - lastBreak) + "ms");
        }
        
        data.setLastBlockBreakTime(now);
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation());
        }
    }
}