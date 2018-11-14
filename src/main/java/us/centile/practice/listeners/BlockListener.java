package us.centile.practice.listeners;

import us.centile.practice.*;
import org.bukkit.event.player.*;
import us.centile.practice.arena.*;
import org.bukkit.event.*;
import org.bukkit.block.*;
import us.centile.practice.player.*;
import us.centile.practice.duel.*;
import org.bukkit.event.block.*;

public class BlockListener implements Listener
{
    private nPractice plugin;
    
    public BlockListener(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockForm(final BlockSpreadEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().closest(event.getBlockClicked().getLocation());
        if (arena != null) {
            arena.getBlockChangeTracker().add(event.getBlockClicked().getRelative(event.getBlockFace()).getState());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockForm(final BlockFromToEvent event) {
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().closest(event.getToBlock().getLocation());
        if (arena != null) {
            arena.getBlockChangeTracker().add(event.getToBlock().getState());
            arena.getBlockChangeTracker().add(event.getToBlock().getRelative(BlockFace.DOWN).getState());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            return;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(e.getPlayer().getUniqueId());
        if (duel != null) {
            final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName());
            if (arena.getBlockChangeTracker().isPlayerPlacedBlock(e.getBlock().getLocation())) {
                arena.getBlockChangeTracker().add(e.getBlock().getState());
                return;
            }
        }
        e.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent e) {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(e.getPlayer());
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            return;
        }
        final Duel duel = this.plugin.getManagerHandler().getDuelManager().getDuelFromPlayer(e.getPlayer().getUniqueId());
        if (duel == null) {
            e.setCancelled(true);
            return;
        }
        final Arena arena = this.plugin.getManagerHandler().getArenaManager().getArena(duel.getArenaName());
        final double averageY = (arena.getFirstTeamLocation().getY() + arena.getSecondTeamLocation().getY()) / 2.0;
        if (Math.abs(e.getBlock().getY() - averageY) > 5.0) {
            e.setCancelled(true);
            return;
        }
        arena.getBlockChangeTracker().setPlayerPlacedBlock(e.getBlock().getLocation());
        arena.getBlockChangeTracker().add(e.getBlockReplacedState());
    }
}
