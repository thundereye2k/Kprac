package us.centile.practice.runnables;

import org.bukkit.entity.*;
import us.centile.practice.player.*;
import us.centile.practice.*;

public class RemovePlayerTask implements Runnable
{
    private Player player;
    
    public RemovePlayerTask(final Player player) {
        this.player = player;
    }
    
    @Override
    public void run() {
        final PracticePlayer profile = PracticePlayer.getByUuid(this.player.getUniqueId());
        if (profile != null) {
            profile.setGlobalPersonalElo(nPractice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, true));
            profile.setGlobalPremiumElo(nPractice.getInstance().getManagerHandler().getPracticePlayerManager().getPremiumElo(profile));
            profile.setGlobalPartyElo(nPractice.getInstance().getManagerHandler().getPracticePlayerManager().getGlobalElo(profile, false));
            profile.save();
            PracticePlayer.getProfiles().remove(profile);
        }
    }
}
