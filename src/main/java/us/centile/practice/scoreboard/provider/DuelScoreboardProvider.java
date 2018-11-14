package us.centile.practice.scoreboard.provider;

import us.centile.practice.*;
import org.bukkit.entity.*;
import us.centile.practice.scoreboard.*;
import org.bukkit.*;
import us.centile.practice.listeners.*;
import java.text.*;
import com.google.common.collect.*;
import us.centile.practice.tournament.*;
import us.centile.practice.player.*;
import us.centile.practice.duel.*;
import java.util.*;
import org.apache.commons.lang3.time.*;

public class DuelScoreboardProvider extends SidebarProvider
{
    private final nPractice plugin;
    
    public DuelScoreboardProvider(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player paramPlayer) {
        return DuelScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(player.getUniqueId());
        lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.AQUA + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
        if (duel != null) {
            if (duel.getFfaPlayers() == null || duel.getFfaPlayers().size() <= 0) {
                final boolean isParty = duel.getOtherDuelTeam(player).size() >= 2;
                final String opponent = (duel.getOtherDuelTeam(player).get(0) != null) ? Bukkit.getOfflinePlayer(duel.getOtherDuelTeam(player).get(0)).getName() : ("Undefined" + (isParty ? "'s Party" : ""));
                if (opponent != null) {
                    lines.add(new SidebarEntry(ChatColor.RED.toString() + ChatColor.BOLD, "Opponent: ", ChatColor.WHITE + opponent));
                }
            }
            lines.add(new SidebarEntry(ChatColor.DARK_GREEN.toString() + ChatColor.BOLD, "Duration: ", ChatColor.WHITE + this.getRemaining((duel.getEndMatchTime() != 0L) ? duel.getFinalDuration() : duel.getStartDuration())));
            final long now = System.currentTimeMillis();
            final double diff = PlayerListener.getLastPearl().containsKey(player.getUniqueId()) ? (now - PlayerListener.getLastPearl().get(player.getUniqueId())) : ((double)now);
            if (diff < 15000.0) {
                lines.add(new SidebarEntry(ChatColor.GREEN.toString() + ChatColor.BOLD, "Enderpearl: ", ChatColor.WHITE + new DecimalFormat("#0.0").format(15.0 - diff / 1000.0) + "s"));
            }
        }
        else {
            lines.add(new SidebarEntry(nPractice.MAIN_COLOR, ChatColor.BOLD, "Loading..."));
        }
        for (final Tournament tournament : Tournament.getTournaments()) {
            final Set<UUID> counted = Sets.newHashSet();
            for (final TournamentMatch tournamentMatch : tournament.getCurrentMatches()) {
                counted.addAll(tournamentMatch.getMatchPlayers());
            }
            for (final TournamentTeam tournamentTeam : tournament.getCurrentQueue()) {
                counted.addAll(tournamentTeam.getPlayers());
            }
            for (final TournamentTeam tournamentTeam : tournament.getTeams()) {
                counted.addAll(tournamentTeam.getPlayers());
            }
            if (counted.contains(player.getUniqueId())) {
                lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.GREEN + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
                lines.add(new SidebarEntry(nPractice.MAIN_COLOR + ChatColor.BOLD + "Tournament:"));
                lines.add(new SidebarEntry(nPractice.SIDE_COLOR + "" + ((tournament.getTournamentStage() == null) ? "Uncommenced" : tournament.getTournamentStage().toReadable())));
                lines.add(new SidebarEntry(nPractice.EXTRA_COLOR + "[" + counted.size() + "/" + 32 + "]"));
            }
        }
        lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.DARK_AQUA + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
        return lines;
    }
    
    private String getRemaining(final long duration) {
        return DurationFormatUtils.formatDuration(duration, "mm:ss");
    }
}
