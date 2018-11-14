package us.centile.practice.scoreboard;

import org.bukkit.entity.*;
import us.centile.practice.*;
import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;
import org.bukkit.plugin.*;
import us.centile.practice.util.*;
import java.util.*;
import org.bukkit.*;
import us.centile.practice.duel.*;

public class PlayerBoard
{
    public final BufferedObjective bufferedObjective;
    private final Team teammates;
    private final Team opponents;
    private final Team members;
    private final Scoreboard scoreboard;
    private final Player player;
    private final nPractice plugin;
    private boolean sidebarVisible;
    private boolean removed;
    private SidebarProvider defaultProvider;
    private SidebarProvider temporaryProvider;
    private BukkitRunnable runnable;
    
    public PlayerBoard(final nPractice plugin, final Player player) {
        this.sidebarVisible = false;
        this.removed = false;
        this.plugin = plugin;
        this.player = player;
        this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        this.bufferedObjective = new BufferedObjective(this.scoreboard);
        (this.members = this.scoreboard.registerNewTeam("members")).setPrefix(ChatColor.GREEN.toString());
        (this.teammates = this.scoreboard.registerNewTeam("teammates")).setPrefix(ChatColor.GREEN.toString());
        (this.opponents = this.scoreboard.registerNewTeam("opponents")).setPrefix(ChatColor.RED.toString());
        player.setScoreboard(this.scoreboard);
    }
    
    public void remove() {
        this.removed = true;
        if (this.scoreboard != null) {
            synchronized (this.scoreboard) {
                for (final Team team : this.scoreboard.getTeams()) {
                    team.unregister();
                }
                for (final Objective objective : this.scoreboard.getObjectives()) {
                    objective.unregister();
                }
            }
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setSidebarVisible(final boolean visible) {
        this.sidebarVisible = visible;
        this.bufferedObjective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
    
    public void setDefaultSidebar(final SidebarProvider provider, final long updateInterval) {
        if (provider != null && provider.equals(this.defaultProvider)) {
            return;
        }
        this.defaultProvider = provider;
        if (this.runnable != null) {
            this.runnable.cancel();
        }
        if (provider == null) {
            this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return;
        }
        (this.runnable = new BukkitRunnable() {
            public void run() {
                if (PlayerBoard.this.removed) {
                    this.cancel();
                    return;
                }
                if (provider.equals(PlayerBoard.this.defaultProvider)) {
                    PlayerBoard.this.updateObjective();
                }
            }
        }).runTaskTimer((Plugin)this.plugin, updateInterval, updateInterval);
    }
    
    private void updateObjective() {
        final SidebarProvider provider = (this.temporaryProvider != null) ? this.temporaryProvider : this.defaultProvider;
        if (provider == null) {
            this.bufferedObjective.setVisible(false);
        }
        else {
            final List<SidebarEntry> lines = provider.getLines(this.player);
            if (lines == null) {
                this.bufferedObjective.setVisible(false);
                return;
            }
            this.bufferedObjective.setVisible(true);
            this.bufferedObjective.setTitle(provider.getTitle(this.player));
            this.bufferedObjective.setAllLines(lines);
            this.bufferedObjective.flip();
        }
    }
    
    public void addUpdates(final Player playerInDuel) {
        new BukkitRunnable() {
            public void run() {
                final Duel currentDuel = PlayerBoard.this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(playerInDuel.getUniqueId());
                if (currentDuel == null) {
                    for (final Player online : PlayerUtility.getOnlinePlayers()) {
                        if (!PlayerBoard.this.members.hasPlayer((OfflinePlayer)online)) {
                            PlayerBoard.this.members.addPlayer((OfflinePlayer)online);
                        }
                    }
                }
                else if (currentDuel.getFfaPlayers() != null && currentDuel.getFfaPlayers().size() > 0) {
                    final List<UUID> others = currentDuel.getFfaPlayers();
                    for (final UUID uuid : others) {
                        final Player target = Bukkit.getPlayer(uuid);
                        if (target != null && !PlayerBoard.this.opponents.hasPlayer((OfflinePlayer)target)) {
                            PlayerBoard.this.opponents.addPlayer((OfflinePlayer)target);
                        }
                    }
                }
                else {
                    final List<UUID> selfTeam = currentDuel.getDuelTeam(playerInDuel);
                    final List<UUID> otherTeam = currentDuel.getOtherDuelTeam(playerInDuel);
                    if (selfTeam != null) {
                        for (final UUID uuid2 : selfTeam) {
                            final Player target2 = Bukkit.getPlayer(uuid2);
                            if (target2 != null && !PlayerBoard.this.teammates.hasPlayer((OfflinePlayer)target2)) {
                                PlayerBoard.this.teammates.addPlayer((OfflinePlayer)target2);
                            }
                        }
                    }
                    if (otherTeam != null) {
                        for (final UUID uuid2 : otherTeam) {
                            final Player target2 = Bukkit.getPlayer(uuid2);
                            if (target2 != null && !PlayerBoard.this.opponents.hasPlayer((OfflinePlayer)target2)) {
                                PlayerBoard.this.opponents.addPlayer((OfflinePlayer)target2);
                            }
                        }
                    }
                }
            }
        }.runTaskLater((Plugin)this.plugin, 5L);
    }
}
