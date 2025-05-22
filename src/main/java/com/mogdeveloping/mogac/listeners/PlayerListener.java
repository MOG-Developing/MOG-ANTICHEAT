package com.mogdeveloping.mogac.listeners;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.checks.combat.KillAura;
import com.mogdeveloping.mogac.checks.combat.Reach;
import com.mogdeveloping.mogac.checks.movement.Speed;
import com.mogdeveloping.mogac.checks.movement.Spider;
import com.mogdeveloping.mogac.checks.movement.Timer;
import com.mogdeveloping.mogac.checks.world.Destroyer;
import com.mogdeveloping.mogac.checks.world.Scaffold;
import com.mogdeveloping.mogac.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class PlayerListener implements Listener {

    private final MOGAC plugin;

    public PlayerListener(MOGAC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().getPlayerData(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().removePlayerData(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        data.setLastLocation(to);
        data.setLastMoveTime(System.currentTimeMillis());
        data.setOnGround(player.isOnGround());
        
        if (data.isOnGround()) {
            data.setLastOnGroundLocation(to);
        }
        
        plugin.getCheckManager().getChecksForType(CheckType.MOVEMENT).forEach(check -> {
            if (check instanceof Speed) {
                ((Speed) check).checkSpeed(player, from, to);
            } else if (check instanceof Spider) {
                ((Spider) check).checkSpider(player, from, to);
            } else if (check instanceof Timer) {
                ((Timer) check).checkTimer(player, from, to);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        data.setTeleporting(true);
        
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getPlayerDataManager().hasPlayerData(player.getUniqueId())) {
                data.setTeleporting(false);
            }
        }, 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        data.setLastVelocity(event.getVelocity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            
            data.setLastAttackTime(System.currentTimeMillis());
            
            plugin.getCheckManager().getChecksForType(CheckType.COMBAT).forEach(check -> {
                if (check instanceof KillAura) {
                    KillAura killAura = (KillAura) check;
                    killAura.checkMultiAura(player, event);
                    killAura.checkRotation(player, event.getEntity());
                    killAura.checkLineOfSight(player, event.getEntity());
                } else if (check instanceof Reach) {
                    ((Reach) check).checkReach(player, event.getEntity());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        
        plugin.getCheckManager().getChecksForType(CheckType.WORLD).forEach(check -> {
            if (check instanceof Scaffold) {
                ((Scaffold) check).checkScaffold(player, event);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        
        Player player = event.getPlayer();
        
        plugin.getCheckManager().getChecksForType(CheckType.WORLD).forEach(check -> {
            if (check instanceof Destroyer) {
                ((Destroyer) check).checkDestroyer(player, event);
            }
        });
    }
}