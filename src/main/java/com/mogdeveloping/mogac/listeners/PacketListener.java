package com.mogdeveloping.mogac.listeners;

import com.mogdeveloping.mogac.MOGAC;
import com.mogdeveloping.mogac.checks.CheckType;
import com.mogdeveloping.mogac.checks.packet.PacketSpammer;
import com.mogdeveloping.mogac.data.PlayerData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketListener implements Listener {

    private final MOGAC plugin;
    private final Map<UUID, Channel> channels = new HashMap<>();
    private Field playerConnectionField;
    private Field networkManagerField;
    private Field channelField;
    private Method getHandleMethod;

    public PacketListener(MOGAC plugin) {
        this.plugin = plugin;
        setup();
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            inject(player);
        }
    }

    private void setup() {
        try {
            Class<?> craftPlayerClass = getCraftBukkitClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = getNMSClass("EntityPlayer");
            Class<?> playerConnectionClass = getNMSClass("PlayerConnection");
            Class<?> networkManagerClass = getNMSClass("NetworkManager");
            
            getHandleMethod = craftPlayerClass.getMethod("getHandle");
            playerConnectionField = entityPlayerClass.getField("playerConnection");
            networkManagerField = playerConnectionClass.getField("networkManager");
            channelField = networkManagerClass.getDeclaredField("channel");
            channelField.setAccessible(true);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to setup packet listener: " + e.getMessage());
        }
    }

    private Class<?> getNMSClass(String name) throws ClassNotFoundException {
        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("net.minecraft.server." + version + "." + name);
    }

    private Class<?> getCraftBukkitClass(String name) throws ClassNotFoundException {
        String version = plugin.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        uninject(event.getPlayer());
    }

    private void inject(Player player) {
        try {
            Object entityPlayer = getHandleMethod.invoke(player);
            Object playerConnection = playerConnectionField.get(entityPlayer);
            Object networkManager = networkManagerField.get(playerConnection);
            Channel channel = (Channel) channelField.get(networkManager);
            
            if (channels.containsKey(player.getUniqueId())) {
                return;
            }
            
            channels.put(player.getUniqueId(), channel);
            
            channel.pipeline().addBefore("packet_handler", "mogac_packet_handler", new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    try {
                        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
                        data.incrementPacketsSent();
                        data.setLastPacketTime(System.currentTimeMillis());
                        
                        plugin.getCheckManager().getChecksForType(CheckType.PACKET).forEach(check -> {
                            if (check instanceof PacketSpammer) {
                                ((PacketSpammer) check).checkPackets(player, msg);
                            }
                        });
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error in packet handler: " + e.getMessage());
                    }
                    
                    super.channelRead(ctx, msg);
                }
                
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    super.write(ctx, msg, promise);
                }
            });
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to inject player " + player.getName() + ": " + e.getMessage());
        }
    }

    private void uninject(Player player) {
        Channel channel = channels.remove(player.getUniqueId());
        
        if (channel == null) {
            return;
        }
        
        if (channel.pipeline().get("mogac_packet_handler") != null) {
            channel.pipeline().remove("mogac_packet_handler");
        }
    }
}