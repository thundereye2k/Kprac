package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import us.centile.practice.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class MatchesCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public MatchesCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.matches.admin")) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("Incorrect Usage.");
            return true;
        }
        if (args.length == 2) {
            String name = "";
            final Player player = Bukkit.getPlayer(args[0]);
            UUID uuid;
            if (player != null) {
                uuid = player.getUniqueId();
                name = player.getName();
            }
            else {
                try {
                    final Map.Entry<UUID, String> recipient = PracticePlayer.getExternalPlayerInformation(args[0]);
                    uuid = recipient.getKey();
                    name = recipient.getValue();
                }
                catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Failed to find player.");
                    return true;
                }
            }
            final int amount = Integer.parseInt(args[1]);
            final PracticePlayer practicePlayer = PracticePlayer.getByUuid(uuid);
            if (practicePlayer == null) {
                return true;
            }
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new Runnable() {
                @Override
                public void run() {
                    practicePlayer.setPremiumTokens(practicePlayer.getPremiumTokens() + amount);
                    practicePlayer.save();
                }
            });
            sender.sendMessage(ChatColor.YELLOW + "Given " + ChatColor.GREEN + amount + ChatColor.YELLOW + " matches to " + ChatColor.GREEN + name + ChatColor.YELLOW + ".");
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.YELLOW + "You have received " + amount + " premium matches.");
            }
        }
        else {
            sender.sendMessage("Incorrect Usage.");
        }
        return true;
    }
}
