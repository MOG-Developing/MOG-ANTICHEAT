package com.mogdeveloping.mogac.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;

import java.util.List;

public class MathUtil {

    public static double getHorizontalDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static double getVerticalDistance(Location from, Location to) {
        return Math.abs(to.getY() - from.getY());
    }
    
    public static double getDistance3D(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double getReachDistance(Player player, Entity target) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        
        Location targetLocation = target.getLocation();
        Vector playerToTarget = targetLocation.toVector().subtract(eyeLocation.toVector());
        
        double dot = playerToTarget.dot(direction);
        Vector projection = direction.clone().multiply(dot);
        
        Vector difference = playerToTarget.subtract(projection);
        double distance = difference.length();
        
        return distance;
    }
    
    public static double getDirectDistance(Player player, Entity target) {
        return player.getLocation().distance(target.getLocation());
    }
    
    public static boolean hasLineOfSight(Player player, Location location) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = location.toVector().subtract(eyeLocation.toVector());
        double distance = direction.length();
        direction.normalize();
        
        Block block = eyeLocation.getBlock();
        double step = 0.1;
        
        for (double d = 0; d < distance; d += step) {
            Location checkLoc = eyeLocation.clone().add(direction.clone().multiply(d));
            block = checkLoc.getBlock();
            if (block.getType().isSolid()) {
                return false;
            }
        }
        return true;
    }
    
    public static double getAngle(Vector a, Vector b) {
        double dot = a.dot(b);
        double magnitude = a.length() * b.length();
        return Math.acos(dot / magnitude);
    }
    
    public static boolean isSimilar(double a, double b, double threshold) {
        return Math.abs(a - b) < threshold;
    }
    
    public static Vector getDirection(Location location) {
        double yaw = Math.toRadians(location.getYaw());
        double pitch = Math.toRadians(location.getPitch());
        
        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch);
        double z = Math.cos(yaw) * Math.cos(pitch);
        
        return new Vector(x, y, z);
    }
    
    public static boolean isLookingAt(Player player, Location target, double precision) {
        Vector direction = getDirection(player.getLocation());
        Vector targetVector = target.toVector().subtract(player.getEyeLocation().toVector()).normalize();
        
        return direction.angle(targetVector) < precision;
    }
    
    public static double calculateYawDifference(float yaw1, float yaw2) {
        double diff = Math.abs(yaw1 - yaw2) % 360;
        return diff > 180 ? 360 - diff : diff;
    }
    
    public static double getPitchDifference(float pitch1, float pitch2) {
        return Math.abs(pitch1 - pitch2);
    }
    
    public static double getAverageDouble(List<Double> values) {
        if (values.isEmpty()) return 0;
        
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
}