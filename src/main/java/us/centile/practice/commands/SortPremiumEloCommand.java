package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.scheduler.*;
import us.centile.practice.player.*;
import com.mongodb.*;
import org.bson.conversions.*;
import org.bson.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.plugin.*;

public class SortPremiumEloCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public SortPremiumEloCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        new BukkitRunnable() {
            public void run() {
                final List<Document> documents = (List<Document>)PracticePlayer.main.getPracticeDatabase().getProfiles().find().limit(10).sort(new BasicDBObject("globalPremiumElo", -1)).into(new ArrayList());
                sender.sendMessage(ChatColor.WHITE + "Listing top premium elos ");
                int index = 1;
                for (final Document document : documents) {
                    final UUID uuid = UUID.fromString(document.getString("uuid"));
                    final String name = Bukkit.getOfflinePlayer(uuid).getName();
                    final int premiumElo = document.getInteger("globalPremiumElo");
                    sender.sendMessage(ChatColor.AQUA.toString() + index++ + ". " + name + ": " + premiumElo);
                }
            }
        }.runTaskAsynchronously(this.plugin);
        return true;
    }
}
