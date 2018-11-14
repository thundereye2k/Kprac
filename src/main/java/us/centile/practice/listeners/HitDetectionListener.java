package us.centile.practice.listeners;

import us.centile.practice.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class HitDetectionListener implements Listener
{
    private final nPractice plugin;
    
    public HitDetectionListener(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, nPractice.getInstance());
        for (final Player player : Bukkit.getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(19);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        event.getPlayer().setMaximumNoDamageTicks(19);
    }
}
