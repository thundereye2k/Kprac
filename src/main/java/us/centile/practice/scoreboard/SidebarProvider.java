package us.centile.practice.scoreboard;

import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.*;
import com.google.common.base.*;

public abstract class SidebarProvider
{
    public static String SCOREBOARD_TITLE;
    protected static final String STRAIGHT_LINE;
    
    public abstract String getTitle(final Player p0);
    
    public abstract List<SidebarEntry> getLines(final Player p0);
    
    static {
        STRAIGHT_LINE = ChatColor.STRIKETHROUGH.toString() + Strings.repeat("-", 256).substring(0, 10);
    }
}
