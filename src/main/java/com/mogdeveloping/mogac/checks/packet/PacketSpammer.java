package com.mogdeveloping.mogac.checks.packet;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketSpammer extends Check {

    private final Map<UUID, Integer> packetCount = new HashMap<>();
    private final Map<UUID, Long> lastPacketTime = new HashMap<>();
    
    private static final int PACKET_LIMIT = 100;
    private static final long TIME_PERIOD = 1000;

    public PacketSpammer(MOGAC plugin) {
        super(plugin, "PacketSpammer", CheckType.PACKET);
    }

    public void checkPackets(Player player, Object packet) {
        if (player == null) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < 5000) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        
        if (!lastPacketTime.containsKey(uuid)) {
            lastPacketTime.put(uuid, now);
            packetCount.put(uuid, 1);
            return;
        }
        
        long lastTime = lastPacketTime.get(uuid);
        int count = packetCount.get(uuid);
        
        if (now - lastTime < TIME_PERIOD) {
            count++;
        } else {
            if (count > PACKET_LIMIT) {
                flag(player, "Packets: " + count + " > " + PACKET_LIMIT + " in " + TIME_PERIOD + "ms");
            }
            
            lastPacketTime.put(uuid, now);
            count = 1;
        }
        
        packetCount.put(uuid, count);
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations) {
            player.kickPlayer("[MOG-AC] Too many packets sent to the server");
        }
    }
}