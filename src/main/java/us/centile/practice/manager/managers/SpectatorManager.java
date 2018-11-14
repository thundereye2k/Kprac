package us.centile.practice.manager.managers;

import us.centile.practice.manager.*;
import org.bukkit.entity.*;
import us.centile.practice.player.*;
import us.centile.practice.util.*;
import us.centile.practice.duel.*;
import java.util.*;

public class SpectatorManager extends Manager
{
    private HashSet<UUID> spectators;
    private Map<UUID, UUID> spectatingMatchUUID;
    
    public SpectatorManager(final ManagerHandler handler) {
        super(handler);
        this.spectators = new HashSet<UUID>();
        this.spectatingMatchUUID = new HashMap<UUID, UUID>();
    }
    
    public void toggleSpectatorMode(final Player player) {
        if (this.spectators.contains(player.getUniqueId())) {
            this.spectators.remove(player.getUniqueId());
            this.removeSpectator(player, true);
            return;
        }
        final PracticePlayer practicePlayer = this.handler.getPracticePlayerManager().getPracticePlayer(player);
        this.spectators.add(player.getUniqueId());
        practicePlayer.setCurrentState(PlayerState.SPECTATING);
        player.getInventory().setContents(this.handler.getItemManager().getSpectatorModeItems());
        player.updateInventory();
        player.setAllowFlight(true);
        player.setFlying(true);
    }
    
    public void removeSpectatorMode(final Player player) {
        if (this.spectators.contains(player.getUniqueId())) {
            this.spectators.remove(player.getUniqueId());
            this.removeSpectator(player, true);
        }
    }
    
    public void addSpectator(final Player player, final Player target) {
        final Duel duel = this.handler.getDuelManager().getDuelFromPlayer(target.getUniqueId());
        if (duel == null) {
            return;
        }
        this.spectatingMatchUUID.put(player.getUniqueId(), duel.getUUID());
        duel.addSpectator(player.getUniqueId());
        if (duel.getFfaPlayers() != null) {
            for (final UUID uuid : duel.getFfaPlayersAlive()) {
                final Player player2 = this.handler.getPlugin().getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                player.showPlayer(player2);
                player2.hidePlayer(player);
            }
        }
        else {
            for (final UUID uuid : duel.getFirstTeamAlive()) {
                final Player player2 = this.handler.getPlugin().getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                player.showPlayer(player2);
                player2.hidePlayer(player);
            }
            for (final UUID uuid : duel.getSecondTeamAlive()) {
                final Player player2 = this.handler.getPlugin().getServer().getPlayer(uuid);
                if (player2 == null) {
                    continue;
                }
                player.showPlayer(player2);
                player2.hidePlayer(player);
            }
        }
        UtilPlayer.clear(player);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(target.getLocation().add(0.0, 3.5, 0.0));
        player.getInventory().setContents(this.handler.getItemManager().getSpecItems());
        player.updateInventory();
    }
    
    public void removeSpectator(final Player player, final boolean forced) {
        if (this.spectatingMatchUUID.containsKey(player.getUniqueId())) {
            final Duel duel = this.handler.getDuelManager().getDuelByUUID(this.spectatingMatchUUID.get(player.getUniqueId()));
            if (duel != null && forced) {
                duel.getSpectators().remove(player.getUniqueId());
            }
        }
        this.spectatingMatchUUID.remove(player.getUniqueId());
        this.handler.getPracticePlayerManager().sendToLobby(player);
    }
    
    public boolean isSpectatorMode(final Player player) {
        return this.spectators.contains(player.getUniqueId());
    }
}
