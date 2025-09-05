package com.mogdeveloping.mogac.checks.world;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.data.PlayerData;
import com.mogdeveloping.mogac.utils.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Scaffold extends Check {

    private final Map<UUID, Float> lastYaw = new HashMap<>();
    private final Map<UUID, Float> lastPitch = new HashMap<>();

    public Scaffold(MOGAC plugin) {
        super(plugin, "Scaffold", CheckType.WORLD);
    }

    public void checkScaffold(Player player, BlockPlaceEvent event) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        int exemptJoin = plugin.getConfig().getInt("checks.Scaffold.exempt-join-seconds", 6);
        if (data.isTeleporting() || System.currentTimeMillis() - data.getJoinTime() < exemptJoin * 1000L) {
            return;
        }

        Block block = event.getBlock();
        BlockFace face = event.getBlockAgainst().getFace(block);

        checkLookingAtBlock(player, block, face);
        checkPlaceSpeed(player);
        checkExpandedScaffold(player, block);
    }

    private void checkLookingAtBlock(Player player, Block block, BlockFace face) {
        Location blockCenter = block.getLocation().add(0.5, 0.5, 0.5);
        double threshold = plugin.getConfig().getDouble("checks.Scaffold.require-looking-threshold", 0.28);
        boolean isLookingAt = MathUtil.isLookingAt(player, blockCenter, threshold);

        if (!isLookingAt && face != BlockFace.UP && face != BlockFace.DOWN) {
            flag(player, "Not looking at block");
        }
    }

    private void checkPlaceSpeed(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        long now = System.currentTimeMillis();
        long lastPlace = data.getLastBlockPlaceTime();

        long minInterval = plugin.getConfig().getLong("checks.Scaffold.min-place-interval-ms", 120L);
        if (lastPlace > 0 && now - lastPlace < minInterval) {
            flag(player, "Fast place: " + (now - lastPlace) + "ms");
        }

        data.setLastBlockPlaceTime(now);
    }

    private void checkExpandedScaffold(Player player, Block placedBlock) {
        UUID uuid = player.getUniqueId();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        if (lastYaw.containsKey(uuid) && lastPitch.containsKey(uuid)) {
            float lastYawValue = lastYaw.get(uuid);
            float lastPitchValue = lastPitch.get(uuid);

            double yawDiff = MathUtil.calculateYawDifference(yaw, lastYawValue);
            double pitchDiff = MathUtil.getPitchDifference(pitch, lastPitchValue);

            if (pitchDiff > 30 && player.getLocation().getPitch() > 50) {
                flag(player, "Expanded scaffold rotation: Y:" + String.format("%.1f", yawDiff) +
                        ", P:" + String.format("%.1f", pitchDiff));
            }
        }

        lastYaw.put(uuid, yaw);
        lastPitch.put(uuid, pitch);
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.teleport(player.getLocation());
        }
    }
}