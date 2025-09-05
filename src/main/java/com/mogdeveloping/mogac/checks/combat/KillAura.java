package com.mogdeveloping.mogac.checks.combat;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.Check;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.utils.MathUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAura extends Check {

    private final Map<UUID, Integer> attackCounts = new HashMap<>();
    private final Map<UUID, Long> lastAttackTime = new HashMap<>();
    private final Map<UUID, Float> lastYaw = new HashMap<>();
    private final Map<UUID, Float> lastPitch = new HashMap<>();

    public KillAura(MOGAC plugin) {
        super(plugin, "KillAura", CheckType.COMBAT);
    }

    public void checkMultiAura(Player player, EntityDamageByEntityEvent event) {
        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        int count = attackCounts.getOrDefault(uuid, 0) + 1;
        attackCounts.put(uuid, count);

        long lastTime = lastAttackTime.getOrDefault(uuid, 0L);

        int multiPerSecond = plugin.getConfig().getInt("checks.KillAura.multi-entities-per-second", 3);
        if (now - lastTime > 1000) {
            if (count > multiPerSecond) {
                flag(player, "MultiAura (hit " + count + " entities in 1s)");
            }
            attackCounts.put(uuid, 0);
        }

        lastAttackTime.put(uuid, now);
    }

    public void checkRotation(Player player, Entity target) {
        UUID uuid = player.getUniqueId();
        float yaw = player.getLocation().getYaw();
        float pitch = player.getLocation().getPitch();

        if (lastYaw.containsKey(uuid) && lastPitch.containsKey(uuid)) {
            float lastYawValue = lastYaw.get(uuid);
            float lastPitchValue = lastPitch.get(uuid);

            double yawDiff = MathUtil.calculateYawDifference(yaw, lastYawValue);
            double pitchDiff = MathUtil.getPitchDifference(pitch, lastPitchValue);
            double rotThresh = plugin.getConfig().getDouble("checks.KillAura.rotation-diff-threshold", 35.0);

            double fov = plugin.getConfig().getDouble("checks.KillAura.fov-threshold", 0.32);
            boolean isLookingAt = MathUtil.isLookingAt(player, target.getLocation(), fov);

            if (!isLookingAt && (yawDiff > rotThresh || pitchDiff > rotThresh)) {
                flag(player, "Rotation (Y:" + String.format("%.1f", yawDiff) +
                        ", P:" + String.format("%.1f", pitchDiff) + ", Not looking at target)");
            }
        }

        lastYaw.put(uuid, yaw);
        lastPitch.put(uuid, pitch);
    }

    public void checkLineOfSight(Player player, Entity target) {
        if (!MathUtil.hasLineOfSight(player, target.getLocation())) {
            flag(player, "Line of Sight (hitting through walls/blocks)");
        }
    }

    @Override
    public void onViolation(Player player, double vl, String details) {
        if (vl >= maxViolations / 2) {
            player.setVelocity(player.getVelocity().multiply(0.5));
        }
    }
}