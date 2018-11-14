package us.centile.practice.runnables;

import us.centile.practice.*;
import us.centile.practice.kit.*;
import us.centile.practice.util.*;
import org.bukkit.plugin.java.*;
import org.bukkit.inventory.*;
import us.centile.practice.player.*;
import us.centile.practice.tournament.*;
import org.bukkit.*;
import java.util.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;

public class LoadPlayerTask implements Runnable
{
    private PracticePlayer practicePlayer;
    private nPractice plugin;
    
    public LoadPlayerTask(final nPractice plugin, final PracticePlayer practicePlayer) {
        this.plugin = plugin;
        this.practicePlayer = practicePlayer;
    }
    
    @Override
    public void run() {
        if (this.practicePlayer == null) {
            return;
        }
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            if (!this.practicePlayer.getEloMap().containsKey(kit.getName())) {
                this.practicePlayer.addElo(kit.getName(), 1000);
            }
            if (!this.practicePlayer.getPremiumEloMap().containsKey(kit.getName())) {
                this.practicePlayer.addPremiumElo(kit.getName(), 1000);
            }
            if (!this.practicePlayer.getPartyEloMap().containsKey(this.practicePlayer.getUUID())) {
                this.practicePlayer.getPartyEloMap().put(this.practicePlayer.getUUID(), new HashMap<String, Integer>());
            }
            this.practicePlayer.addPartyElo(this.practicePlayer.getUUID(), kit.getName(), 1000);
        }
        final Config config = new Config(this.plugin, "/players", this.practicePlayer.getUUID().toString());
        final ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");
        if (playerKitsSection != null) {
            for (final Kit kit2 : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
                final ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit2.getName());
                if (kitSection != null) {
                    for (final String kitKey : kitSection.getKeys(false)) {
                        final Integer kitIndex = Integer.parseInt(kitKey);
                        final String displayName = kitSection.getString(kitKey + ".displayName");
                        final ItemStack[] mainContents = (ItemStack[]) ((List)kitSection.get(kitKey + ".mainContents")).toArray(new ItemStack[0]);
                        final ItemStack[] armorContents = (ItemStack[]) ((List)kitSection.get(kitKey + ".armorContents")).toArray(new ItemStack[0]);
                        final PlayerKit playerKit = new PlayerKit(kit2.getName(), kitIndex, displayName, mainContents, armorContents);
                        this.practicePlayer.addKit(kit2.getName(), kitIndex, playerKit);
                    }
                }
            }
        }
        this.practicePlayer.setCurrentState(PlayerState.LOBBY);
        final Player player = Bukkit.getPlayer(this.practicePlayer.getUUID());
        if (player != null && Tournament.getTournaments().size() > 0) {
            player.sendMessage(ChatColor.YELLOW + "\u00c2�l(ACTIVE) \u00c2�7Tournament is currently available. (/join)");
        }
    }
}
