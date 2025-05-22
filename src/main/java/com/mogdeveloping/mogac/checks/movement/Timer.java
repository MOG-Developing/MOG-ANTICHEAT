package com.mogdeveloping.mogac.checks.movement;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Timer extends Check {

    private final Map<UUID, Long> lastMovePacket = new HashMap<>();
    private final Map<UUID, Integer> movementCount = new HashMap<>();
    private final Map<UUID, Long> timerCheckTime = new HashMap<>();

    private static final int MOVE_THRESHOLD = 20;
    private static final long TIME_PERIOD = 1000;

    public Timer(MOGAC plugin) {
        super(plugin, "Timer", CheckType.MOVEMENT);
    }

    public void checkTimer(Player player, Location from, Location to) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        
        if (!lastMovePacket.containsKey(uuid)) {
            lastMovePacket.put(uuid, now);
            movementCount.put(uuid, 0);
            timerCheckTime.put(uuid, now);
            return;
        }
        
        long lastTime = lastMovePacket.get(uuid);
        int count = movementCount.get(uuid);
        long checkTime = timerCheckTime.get(uuid);
        
        if (now - lastTime < 5 && now - lastTime > 0) {
            count++;
        }
        
        if (now - checkTime >= TIME_PERIOD) {
            if (count > MOVE_THRESHOLD) {
                flag(player, "Packets: " + count + " > " + MOVE_THRESHOLD + " in " + TIME_PERIOD + "ms");
            }
            
            count = 0;
            timerCheckTime.put(uuid, now);
        }
        
        lastMovePacket.put(uuid, now);
        movementCount.put(uuid, count);
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation());
        }
    }
}