package us.centile.practice.runnables;

import us.centile.practice.*;
import us.centile.practice.util.*;
import org.bukkit.plugin.java.*;
import us.centile.practice.kit.*;
import us.centile.practice.player.*;
import java.util.*;

public class SavePlayerConfig implements Runnable
{
    private UUID uuid;
    private nPractice plugin;
    
    public SavePlayerConfig(final UUID uuid, final nPractice plugin) {
        this.uuid = uuid;
        this.plugin = plugin;
    }
    
    @Override
    public void run() {
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(this.uuid);
        if (practicePlayer == null) {
            return;
        }
        final Config config = new Config(this.plugin, "/players", this.uuid.toString());
        for (final Kit kit : this.plugin.getManagerHandler().getKitManager().getKitMap().values()) {
            if (practicePlayer.getKitMap().containsKey(kit.getName())) {
                final Map<Integer, PlayerKit> playerKits = practicePlayer.getKitMap().get(kit.getName());
                for (final PlayerKit playerKit : playerKits.values()) {
                    config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".displayName", (Object)playerKit.getDisplayName());
                    config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".mainContents", (Object)playerKit.getMainContents());
                    config.getConfig().set("playerkits." + kit.getName() + "." + playerKit.getKitIndex() + ".armorContents", (Object)playerKit.getArmorContents());
                }
            }
        }
        config.save();
    }
}
