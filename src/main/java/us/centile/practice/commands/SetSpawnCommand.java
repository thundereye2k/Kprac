package us.centile.practice.commands;

import org.bukkit.event.*;
import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.util.*;
import org.bukkit.*;

public class SetSpawnCommand implements CommandExecutor, Listener
{
    private nPractice plugin;
    
    public SetSpawnCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions.");
            return true;
        }
        this.plugin.getConfig().set("spawn", LocationSerializer.serializeLocation(player.getLocation()));
        this.plugin.saveConfig();
        this.plugin.reloadConfig();
        this.plugin.setSpawn(player.getLocation());
        player.sendMessage(ChatColor.YELLOW + "Spawn is now set.");
        return true;
    }
}
