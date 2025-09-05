package com.mogdeveloping.mogac.data;

import com.mogdeveloping.mogac.checks.Check;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final Player player;
    private final Map<Class<? extends Check>, Integer> violations;
    
    private Location lastLocation;
    private Location lastOnGroundLocation;
    private long lastMoveTime;
    private long lastAttackTime;
    private boolean onGround;
    private boolean wasOnGround;
    private int ticksExisted;
    private int packetsSent;
    private long lastPacketTime;
    private long joinTime;
    private Vector lastVelocity;
    private long lastVelocityTime;
    private long lastBlockPlaceTime;
    private long lastBlockBreakTime;
    private boolean teleporting;

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.player = player;
        this.violations = new HashMap<>();
        this.lastLocation = player.getLocation();
        this.lastOnGroundLocation = player.getLocation();
        this.lastMoveTime = System.currentTimeMillis();
        this.lastAttackTime = System.currentTimeMillis();
        this.onGround = player.isOnGround();
        this.wasOnGround = onGround;
        this.ticksExisted = 0;
        this.packetsSent = 0;
        this.lastPacketTime = System.currentTimeMillis();
        this.joinTime = System.currentTimeMillis();
        this.lastVelocity = new Vector(0, 0, 0);
        this.lastVelocityTime = 0L;
        this.lastBlockPlaceTime = 0L;
        this.lastBlockBreakTime = 0L;
        this.teleporting = false;
    }

    public void incrementViolations(Class<? extends Check> checkClass) {
        violations.put(checkClass, getViolations(checkClass) + 1);
    }

    public int getViolations(Class<? extends Check> checkClass) {
        return violations.getOrDefault(checkClass, 0);
    }

    public void resetViolations(Class<? extends Check> checkClass) {
        violations.put(checkClass, 0);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Location getLastOnGroundLocation() {
        return lastOnGroundLocation;
    }

    public void setLastOnGroundLocation(Location lastOnGroundLocation) {
        this.lastOnGroundLocation = lastOnGroundLocation;
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.wasOnGround = this.onGround;
        this.onGround = onGround;
    }

    public boolean wasOnGround() {
        return wasOnGround;
    }

    public int getTicksExisted() {
        return ticksExisted;
    }

    public void incrementTicksExisted() {
        this.ticksExisted++;
    }

    public int getPacketsSent() {
        return packetsSent;
    }

    public void incrementPacketsSent() {
        this.packetsSent++;
    }

    public void resetPacketsSent() {
        this.packetsSent = 0;
    }

    public long getLastPacketTime() {
        return lastPacketTime;
    }

    public void setLastPacketTime(long lastPacketTime) {
        this.lastPacketTime = lastPacketTime;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public Vector getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(Vector lastVelocity) {
        this.lastVelocity = lastVelocity;
        this.lastVelocityTime = System.currentTimeMillis();
    }

    public long getLastVelocityTime() {
        return lastVelocityTime;
    }

    public long getLastBlockPlaceTime() {
        return lastBlockPlaceTime;
    }

    public void setLastBlockPlaceTime(long lastBlockPlaceTime) {
        this.lastBlockPlaceTime = lastBlockPlaceTime;
    }

    public long getLastBlockBreakTime() {
        return lastBlockBreakTime;
    }

    public void setLastBlockBreakTime(long lastBlockBreakTime) {
        this.lastBlockBreakTime = lastBlockBreakTime;
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }
}