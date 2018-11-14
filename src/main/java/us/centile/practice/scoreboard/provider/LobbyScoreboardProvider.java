package us.centile.practice.scoreboard.provider;

import us.centile.practice.*;
import org.bukkit.entity.*;
import us.centile.practice.scoreboard.*;
import org.bukkit.*;
import us.centile.practice.util.*;
import us.centile.practice.player.*;
import com.google.common.collect.*;
import us.centile.practice.tournament.*;
import java.util.*;
import us.centile.practice.duel.*;

public class LobbyScoreboardProvider extends SidebarProvider
{
    private final nPractice plugin;
    
    public LobbyScoreboardProvider(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle(final Player player) {
        return LobbyScoreboardProvider.SCOREBOARD_TITLE;
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer == null || !practicePlayer.getSettings().isScoreboard()) {
            return null;
        }
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.AQUA + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
        lines.add(new SidebarEntry(nPractice.MAIN_COLOR, "Online: ", nPractice.EXTRA_COLOR + "" + PlayerUtility.getOnlinePlayers().size()));
        lines.add(new SidebarEntry(nPractice.MAIN_COLOR, "In Fights: ", nPractice.EXTRA_COLOR + "" + this.getTotalInGame()));
        lines.add(new SidebarEntry(nPractice.MAIN_COLOR, "In Queue: ", nPractice.EXTRA_COLOR + "" + this.plugin.getManagerHandler().getQueueManager().getTotalInQueues()));
        lines.add(new SidebarEntry(nPractice.SIDE_COLOR + "Premium", " Matches: ", nPractice.EXTRA_COLOR + "" + PracticePlayer.getByUuid(player.getUniqueId()).getPremiumTokens()));
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
            lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.GREEN + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
            lines.add(new SidebarEntry(nPractice.MAIN_COLOR + ChatColor.BOLD + "Tournament:"));
            lines.add(new SidebarEntry(nPractice.SIDE_COLOR + "" + ((tournament.getTournamentStage() == null) ? "Uncommenced" : tournament.getTournamentStage().toReadable())));
            lines.add(new SidebarEntry(nPractice.EXTRA_COLOR + "[" + counted.size() + "/" + 32 + "]"));
        }
        lines.add(new SidebarEntry(ChatColor.GRAY, ChatColor.DARK_AQUA + ChatColor.GRAY.toString() + SidebarProvider.STRAIGHT_LINE, SidebarProvider.STRAIGHT_LINE));
        return lines;
    }
    
    private int getTotalInGame() {
        int count = 0;
        for (final Duel duel : this.plugin.getManagerHandler().getDuelManager().getUuidIdentifierToDuel().values()) {
            if (duel.getFirstTeam() != null) {
                count += duel.getFirstTeam().size();
            }
            if (duel.getSecondTeam() != null) {
                count += duel.getSecondTeam().size();
            }
            if (duel.getFfaPlayers() != null) {
                count += duel.getFfaPlayers().size();
            }
        }
        return count;
    }
}
