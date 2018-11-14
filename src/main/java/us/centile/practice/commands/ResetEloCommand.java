package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.util.*;
import org.bukkit.*;
import us.centile.practice.kit.*;
import us.centile.practice.player.*;
import java.util.*;

public class ResetEloCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public ResetEloCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        if (practicePlayer.getCredits() <= 0) {
            player.sendMessage(ChatColor.RED + "You don't have enough credits.");
            return true;
        }
        practicePlayer.setCredits(practicePlayer.getCredits() - 1);
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            practicePlayer.addElo(kit.getName(), 1000);
            practicePlayer.addPartyElo(practicePlayer.getUUID(), kit.getName(), 1000);
        }
        player.sendMessage(ChatColor.GREEN + "Your ELO has been reset.");
        return true;
    }
}
