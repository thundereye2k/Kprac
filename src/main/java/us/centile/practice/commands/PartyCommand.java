package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.tournament.*;
import us.centile.practice.util.*;
import us.centile.practice.player.*;
import java.util.*;
import us.centile.practice.party.*;

public class PartyCommand implements CommandExecutor
{
    private final String[] HELP_COMMAND;
    private nPractice plugin;
    
    public PartyCommand(final nPractice plugin) {
        this.HELP_COMMAND = new String[] { ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Commands:", ChatColor.GOLD + "(*) /party help " + ChatColor.GRAY + "- Displays the help menu", ChatColor.GOLD + "(*) /party create " + ChatColor.GRAY + "- Creates a party instance", ChatColor.GOLD + "(*) /party leave " + ChatColor.GRAY + "- Leave your current party", ChatColor.GOLD + "(*) /party info " + ChatColor.GRAY + "- Displays your party information", ChatColor.GOLD + "(*) /party join (player) " + ChatColor.GRAY + "- Join a party (invited or unlocked)", "", ChatColor.RED + "Leader Commands:", ChatColor.GOLD + "(*) /party open " + ChatColor.GRAY + "- Open your party for others to join", ChatColor.GOLD + "(*) /party lock " + ChatColor.GRAY + "- Lock your party for others to join", ChatColor.GOLD + "(*) /party invite (player) " + ChatColor.GRAY + "- Invites a player to your party", ChatColor.GOLD + "(*) /party kick (player) " + ChatColor.GRAY + "- Kicks a player from your party", ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        final Player player = (Player)sender;
        if (args.length == 0) {
            player.sendMessage(this.HELP_COMMAND);
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
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
        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(this.HELP_COMMAND);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                player.sendMessage(ChatColor.RED + "You are already in a party!");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().createParty(player.getUniqueId(), player.getName());
            player.sendMessage(ChatColor.YELLOW + "You have created a party!");
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().addParty(party);
        }
        else if (args[0].equalsIgnoreCase("info")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            final Player leader = this.plugin.getServer().getPlayer(party.getLeader());
            final StringJoiner members = new StringJoiner(", ");
            for (final UUID memberUUID : party.getMembers()) {
                final Player member = this.plugin.getServer().getPlayer(memberUUID);
                members.add(member.getName());
            }
            final String[] information = { ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------", ChatColor.RED + "Party Information:", ChatColor.GOLD + "Leader: " + ChatColor.GRAY + leader.getName(), ChatColor.GOLD + "Members (" + (party.getMembers().size() + 1) + "): " + ChatColor.GRAY + leader.getName() + ", " + members, ChatColor.GOLD + "Party State: " + ChatColor.GRAY + (party.isOpen() ? "Open" : "Locked"), ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------" };
            player.sendMessage(information);
        }
        else if (args[0].equalsIgnoreCase("leave")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                this.plugin.getManagerHandler().getPartyManager().destroyParty(player.getUniqueId());
                player.sendMessage(ChatColor.YELLOW + "You have disbanded your party!");
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                for (final UUID member2 : party.getMembers()) {
                    final Player pLayer = this.plugin.getServer().getPlayer(member2);
                    pLayer.sendMessage(ChatColor.YELLOW + "The party has been disbanded.");
                    final PracticePlayer ppLayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(pLayer);
                    if (ppLayer.getCurrentState() == PlayerState.LOBBY) {
                        this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(pLayer);
                    }
                }
                this.plugin.getManagerHandler().getInventoryManager().delParty(party);
            }
            else {
                this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.GOLD + "[-] " + ChatColor.GREEN + player.getName() + " has left the party");
                this.plugin.getManagerHandler().getPartyManager().leaveParty(player.getUniqueId());
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
                this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
            }
        }
        else if (args[0].equalsIgnoreCase("open")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (party.isOpen()) {
                    player.sendMessage(ChatColor.RED + "Your party is already open.");
                    return true;
                }
                party.setOpen(true);
                player.sendMessage(ChatColor.YELLOW + "You have opened your party!");
            }
            else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party!");
            }
        }
        else if (args[0].equalsIgnoreCase("lock")) {
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (party.getLeader().equals(player.getUniqueId())) {
                if (!party.isOpen()) {
                    player.sendMessage(ChatColor.RED + "Your party is already locked.");
                    return true;
                }
                party.setOpen(false);
                player.sendMessage(ChatColor.YELLOW + "You have locked your party!");
            }
            else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party!");
            }
        }
        else if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party join <player>");
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId()) != null) {
                player.sendMessage(ChatColor.RED + "You are already in a party!");
                return true;
            }
            final Player target = this.plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            final Party party2 = this.plugin.getManagerHandler().getPartyManager().getParty(target.getUniqueId());
            if (party2 == null || !party2.getLeader().equals(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Party does not exist!");
                return true;
            }
            if (!party2.isOpen()) {
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(player)) {
                    player.sendMessage(Messages.NO_REQUESTS_FOUND);
                    return true;
                }
                if (!this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(player, target)) {
                    player.sendMessage(Messages.NO_REQUESTS_FOUND);
                    return true;
                }
                this.plugin.getManagerHandler().getRequestManager().removePartyRequest(player, target);
            }
            if (party2.getMembers().size() >= 15) {
                player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().joinParty(party2.getLeader(), player.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party2, ChatColor.GOLD + "[+] " + ChatColor.GREEN + player.getName() + " has joined the party");
            player.sendMessage(ChatColor.YELLOW + "You have joined the party!");
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party2);
        }
        else if (args[0].equalsIgnoreCase("kick")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party kick <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (party.getLeader() == target2.getUniqueId()) {
                player.sendMessage(ChatColor.RED + "You can't kick the leader!");
                return true;
            }
            if (!party.getMembers().contains(target2.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This player is not in your party!");
                return true;
            }
            this.plugin.getManagerHandler().getPartyManager().leaveParty(target2.getUniqueId());
            this.plugin.getManagerHandler().getPartyManager().notifyParty(party, ChatColor.GOLD + "[-] " + ChatColor.GREEN + target2.getName() + " has been kicked from the party");
            target2.sendMessage(ChatColor.YELLOW + "You were kicked from the party.");
            final PracticePlayer ptarget = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(target2);
            if (ptarget.getCurrentState() == PlayerState.LOBBY) {
                this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(target2);
            }
            player.sendMessage(ChatColor.GREEN + target2.getName() + ChatColor.YELLOW + " was kicked from your party.");
            this.plugin.getManagerHandler().getInventoryManager().updateParty(party);
        }
        else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Usage: /party invite <player>");
                return true;
            }
            final Party party = this.plugin.getManagerHandler().getPartyManager().getParty(player.getUniqueId());
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party!");
                return true;
            }
            if (!party.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not the leader of this party!");
                return true;
            }
            if (party.isOpen()) {
                player.sendMessage(ChatColor.RED + "This party is open, so anyone can join.");
                return true;
            }
            if (party.getMembers().size() >= 15) {
                player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
                return true;
            }
            final Player target2 = this.plugin.getServer().getPlayer(args[1]);
            if (target2 == null) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND);
                return true;
            }
            if (this.plugin.getManagerHandler().getPartyManager().getParty(target2.getUniqueId()) != null) {
                player.sendMessage(ChatColor.RED + "That player is already in a party");
                return true;
            }
            if (this.plugin.getManagerHandler().getRequestManager().hasPartyRequests(target2) && this.plugin.getManagerHandler().getRequestManager().hasPartyRequestFromPlayer(target2, player)) {
                player.sendMessage(ChatColor.RED + "You have already sent a party invitation to this player, please wait!");
                return true;
            }
            player.sendMessage(ChatColor.YELLOW + "Sent a party request to " + ChatColor.GREEN + target2.getName() + ChatColor.YELLOW + "!");
            this.plugin.getManagerHandler().getRequestManager().addPartyRequest(target2, player);
            final UtilActionMessage actionMessage = new UtilActionMessage();
            actionMessage.addText(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " has invited you to their party! ");
            actionMessage.addText("                                 " + ChatColor.RED + "[Click here to accept]").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/party join " + player.getName());
            actionMessage.sendToPlayer(target2);
        }
        else {
            player.sendMessage(this.HELP_COMMAND);
        }
        return true;
    }
}
