package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import us.centile.practice.tournament.*;
import us.centile.practice.util.*;
import us.centile.practice.events.*;
import org.bukkit.event.*;
import us.centile.practice.player.*;
import us.centile.practice.duel.*;
import us.centile.practice.kit.*;
import java.util.*;
import us.centile.practice.party.*;

public class AcceptCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public AcceptCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(ChatColor.RED + "Unable to accept a duel within your duel");
            return true;
        }
        if (Tournament.getTournaments().size() > 0) {
            for (final Tournament tournament : Tournament.getTournaments()) {
                if (tournament.isInTournament(player)) {
                    player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
                    return true;
                }
            }
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequests(player)) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        final Player target = this.plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Messages.PLAYER_NOT_FOUND);
            return true;
        }
        if (!this.plugin.getManagerHandler().getRequestManager().hasDuelRequestFromPlayer(player, target)) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        final PracticePlayer practiceTarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target);
        if (practiceTarget.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.PLAYER_BUSY);
            return true;
        }
        final DuelRequest request = this.plugin.getManagerHandler().getRequestManager().getDuelRequest(player, target);
        if (request == null) {
            player.sendMessage(Messages.NO_REQUESTS_FOUND);
            return true;
        }
        this.plugin.getManagerHandler().getRequestManager().removeDuelRequest(player, target);
        player.sendMessage(ChatColor.YELLOW + "Accepted duel request from " + ChatColor.GREEN + target.getName());
        final String kitName = request.getKitName();
        final Kit kit = this.plugin.getManagerHandler().getKitManager().getKit(kitName);
        final List<UUID> firstTeam = new ArrayList<UUID>();
        final List<UUID> secondTeam = new ArrayList<UUID>();
        firstTeam.add(player.getUniqueId());
        secondTeam.add(target.getUniqueId());
        final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
        final Party targetParty = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
        if (party != null && targetParty != null) {
            for (final UUID member : party.getMembers()) {
                firstTeam.add(member);
            }
            for (final UUID member : targetParty.getMembers()) {
                secondTeam.add(member);
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, party.getLeader(), targetParty.getLeader(), firstTeam, secondTeam, false));
        }
        else {
            if ((party != null && targetParty == null) || (targetParty != null && party == null)) {
                player.sendMessage(ChatColor.RED + "Either you or the target player is in a party");
                return true;
            }
            this.plugin.getServer().getPluginManager().callEvent((Event)new DuelPreCreateEvent(kit, false, null, null, firstTeam, secondTeam, false));
        }
        return true;
    }
}
