package us.centile.practice.util;

import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.*;

public class PlayerUtility
{
    public static Set<String> getConvertedUuidSet(final Set<UUID> uuids) {
        final Set<String> toReturn = new HashSet<String>();
        for (final UUID uuid : uuids) {
            toReturn.add(uuid.toString());
        }
        return toReturn;
    }
    
    public static List<Player> getOnlinePlayers() {
        final ArrayList<Player> ret = new ArrayList<Player>();
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            ret.add(player);
        }
        return ret;
    }
}
