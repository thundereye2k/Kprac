package us.centile.practice.runnables.other;

import us.centile.practice.*;
import org.bukkit.entity.*;
import us.centile.practice.duel.*;
import us.centile.practice.player.*;

public class InsertMatchTask implements Runnable
{
    private nPractice plugin;
    private Player player;
    private Duel duel;
    private int winningTeamId;
    private String elo_change;
    
    public InsertMatchTask(final nPractice plugin, final Player player, final Duel duel, final int winningTeamId, final String elo_change) {
        this.player = player;
        this.plugin = plugin;
        this.winningTeamId = winningTeamId;
        this.elo_change = elo_change;
    }
    
    @Override
    public void run() {
        final PracticePlayer practicePlayer = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (practicePlayer != null) {}
    }
}
