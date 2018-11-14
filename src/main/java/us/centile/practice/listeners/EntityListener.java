package us.centile.practice.listeners;

import us.centile.practice.*;
import org.bukkit.entity.*;
import us.centile.practice.player.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;

public class EntityListener implements Listener
{
    private nPractice plugin;
    
    public EntityListener(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player damagedPlayer = (Player)e.getEntity();
            final Player attackerPlayer = (Player)e.getDamager();
            final PracticePlayer damagedPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(damagedPlayer);
            final PracticePlayer attackerPracPlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(attackerPlayer);
            if (damagedPracPlayer.getCurrentState() != PlayerState.FIGHTING || attackerPracPlayer.getCurrentState() != PlayerState.FIGHTING) {
                e.setCancelled(true);
            }
            if (damagedPracPlayer.getTeamNumber() == 0) {
                return;
            }
            final int damagedTeamNumber = damagedPracPlayer.getTeamNumber();
            final int attackerTeamNumber = attackerPracPlayer.getTeamNumber();
            if (damagedTeamNumber == attackerTeamNumber) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer((Player)e.getEntity());
            if (practicePlayer.getCurrentState() != PlayerState.FIGHTING) {
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    if (this.plugin.getSpawn() == null) {
                        e.getEntity().teleport(e.getEntity().getWorld().getSpawnLocation());
                    }
                    else {
                        e.getEntity().teleport(this.plugin.getSpawn());
                    }
                }
                e.setCancelled(true);
            }
        }
    }
}
