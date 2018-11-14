package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import us.centile.practice.player.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.*;

public class CreditsCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public CreditsCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!sender.hasPermission("command.credits.admin")) {
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
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    practicePlayer.setCredits(practicePlayer.getCredits() + amount);
                    practicePlayer.save();
                }
            });
            if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
                Bukkit.getPlayer(uuid).sendMessage(ChatColor.GREEN + "You have received (" + amount + ") ELO Reset Credit(s).");
            }
            sender.sendMessage(ChatColor.YELLOW + "Given " + ChatColor.GREEN + amount + ChatColor.YELLOW + " credit(s) to " + ChatColor.GREEN + name + ChatColor.YELLOW + ".");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Incorrect Usage.");
        }
        return true;
    }
}
