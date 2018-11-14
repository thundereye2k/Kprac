package us.centile.practice.schedule;

import org.bukkit.scheduler.*;
import us.centile.practice.*;
import org.bukkit.plugin.*;

public class ScheduleTimer extends BukkitRunnable
{
    private nPractice plugin;
    
    public ScheduleTimer(final nPractice plugin) {
        this.plugin = plugin;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)plugin, (BukkitRunnable)this, 20L, 20L);
    }
    
    public void run() {
        ScheduleHandler.runSchedule();
    }
}
