package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.util.*;
import org.bukkit.*;
import us.centile.practice.runnables.other.*;
import org.bukkit.plugin.*;
import us.centile.practice.player.*;
import us.centile.practice.tournament.*;

public class LeaveCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public LeaveCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY && practicePlayer.getCurrentState() != PlayerState.SPECTATING) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        if (args.length == 0) {
            if (Tournament.getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + "There are no tournaments available.");
                return true;
            }
            final Tournament tournament = Tournament.getTournaments().get(0);
            if (tournament.getTotalPlayersInTournament() == tournament.getPlayersLimit()) {
                player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                return true;
            }
            if (!tournament.isInTournament(player)) {
                player.sendMessage(ChatColor.RED + "You are not in any tournament.");
                return true;
            }
            final TournamentTeam tournamentTeam = tournament.getTournamentTeam(player);
            if (tournamentTeam == null) {
                player.sendMessage(ChatColor.RED + "You are not in any tournament.");
                return true;
            }
            if (tournament.getTournamentMatch(player) != null) {
                player.sendMessage(ChatColor.RED + "You can't leave during a match.");
                return true;
            }
            final String reason = (tournamentTeam.getPlayers().size() > 1) ? "Someone in your party left the tournament" : "You left the tournament";
            tournamentTeam.sendMessage(ChatColor.RED + "You have been removed from the tournament.");
            tournamentTeam.sendMessage(ChatColor.RED + "Reason: " + ChatColor.GRAY + reason);
            tournament.getCurrentQueue().remove(tournamentTeam);
            tournament.getTeams().remove(tournamentTeam);
            this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, (Runnable)new UpdateInventoryTask(this.plugin, UpdateInventoryTask.InventoryTaskType.TOURNAMENT));
        }
        return true;
    }
}
