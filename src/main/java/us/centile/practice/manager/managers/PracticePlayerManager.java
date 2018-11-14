package us.centile.practice.manager.managers;

import us.centile.practice.manager.*;
import us.centile.practice.player.*;
import us.centile.practice.runnables.*;
import org.bukkit.plugin.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.*;
import us.centile.practice.util.*;
import us.centile.practice.scoreboard.provider.*;
import us.centile.practice.scoreboard.*;
import us.centile.practice.kit.*;
import java.util.*;

public class PracticePlayerManager extends Manager
{
    public PracticePlayerManager(final ManagerHandler handler) {
        super(handler);
    }
    
    public void disable() {
        for (final PracticePlayer profile : PracticePlayer.getProfiles()) {
            profile.save();
        }
        for (final Player player : PlayerUtility.getOnlinePlayers()) {
            new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()).run();
        }
    }
    
    private void loadPlayerData(final PracticePlayer player) {
        player.setCurrentState(PlayerState.LOADING);
        Bukkit.getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new LoadPlayerTask(this.handler.getPlugin(), player));
    }
    
    public void createPracticePlayer(final Player player) {
        final PracticePlayer practicePlayer = new PracticePlayer(player.getUniqueId(), true);
        this.sendToLobby(player);
        this.loadPlayerData(practicePlayer);
    }
    
    public PracticePlayer getPracticePlayer(final Player player) {
        return PracticePlayer.getByUuid(player.getUniqueId());
    }
    
    public PracticePlayer getPracticePlayer(final UUID uuid) {
        return PracticePlayer.getByUuid(uuid);
    }
    
    public void removePracticePlayer(final Player player) {
        this.handler.getPlugin().getServer().getScheduler().runTaskAsynchronously(this.handler.getPlugin(), new SavePlayerConfig(player.getUniqueId(), this.handler.getPlugin()));
    }
    
    public void sendToLobby(final Player player) {
        for (final Player onlinePlayer : PlayerUtility.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
            player.showPlayer(onlinePlayer);
        }
        ((CraftPlayer)player).getHandle().p();
        UtilPlayer.clear(player);
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
        if (practicePlayer == null) {
            return;
        }
        Projectile current;
        while ((current = practicePlayer.getProjectileQueue().poll()) != null) {
            current.remove();
        }
        practicePlayer.setCurrentState(PlayerState.LOBBY);
        practicePlayer.setTeamNumber(0);
        if (this.handler.getPlugin().getSpawn() != null) {
            player.teleport(this.handler.getPlugin().getSpawn());
        }
        if (this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
            player.getInventory().setContents(this.handler.getItemManager().getPartyItems());
            player.updateInventory();
        }
        else if (this.handler.getSpectatorManager().isSpectatorMode(player)) {
            player.getInventory().setContents(this.handler.getItemManager().getSpectatorModeItems());
            player.updateInventory();
            practicePlayer.setCurrentState(PlayerState.SPECTATING);
        }
        else {
            player.getInventory().setContents(this.handler.getItemManager().getSpawnItems());
            if (practicePlayer.isShowRematchItemFlag()) {
                practicePlayer.setShowRematchItemFlag(false);
                final ItemStack itemStack = new ItemStack(Material.BLAZE_POWDER);
                UtilItem.name(itemStack, ChatColor.GOLD + "Request Rematch");
                player.getInventory().setItem(3, itemStack);
            }
            player.updateInventory();
        }
        final PlayerBoard playerBoard = this.handler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());
        if (playerBoard != null) {
            playerBoard.setDefaultSidebar(new LobbyScoreboardProvider(this.handler.getPlugin()), 2L);
            playerBoard.setSidebarVisible(practicePlayer.isScoreboard());
        }
    }
    
    public void returnItems(final Player player) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.handler.getPlugin(), new Runnable() {
            @Override
            public void run() {
                UtilPlayer.clear(player);
                final PracticePlayer practicePlayer = PracticePlayer.getByUuid(player.getUniqueId());
                if (practicePlayer != null) {
                    practicePlayer.setCurrentState(PlayerState.LOBBY);
                    practicePlayer.setTeamNumber(0);
                    if (PracticePlayerManager.this.handler.getPartyManager().getParty(player.getUniqueId()) != null) {
                        player.getInventory().setContents(PracticePlayerManager.this.handler.getItemManager().getPartyItems());
                        player.updateInventory();
                    }
                    else {
                        player.getInventory().setContents(PracticePlayerManager.this.handler.getItemManager().getSpawnItems());
                        player.updateInventory();
                    }
                }
            }
        }, 1L);
    }
    
    public int getGlobalElo(final PracticePlayer player, final boolean solo) {
        int i = 0;
        int count = 0;
        for (final Kit kit : this.handler.getKitManager().getKitMap().values()) {
            if (solo) {
                i += player.getEloMap().get(kit.getName());
                ++count;
            }
            else {
                if (!player.getPartyEloMap().containsKey(player.getUUID())) {
                    continue;
                }
                if (!player.getPartyEloMap().get(player.getUUID()).containsKey(kit.getName())) {
                    continue;
                }
                i += player.getPartyEloMap().get(player.getUUID()).get(kit.getName());
                ++count;
            }
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return Math.round(i / count);
    }
    
    public int getPremiumElo(final PracticePlayer player) {
        int i = 0;
        int count = 0;
        for (final Kit kit : this.handler.getKitManager().getKitMap().values()) {
            if (kit.isPremium()) {
                i += player.getPremiumEloMap().get(kit.getName());
                ++count;
            }
        }
        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }
        return Math.round(i / count);
    }
}
