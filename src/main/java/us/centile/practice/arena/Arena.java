package us.centile.practice.arena;

import org.bukkit.*;

public class Arena
{
    private boolean enabled;
    private String name;
    private Location firstTeamLocation;
    private Location secondTeamLocation;
    private boolean open;
    private transient BlockChangeTracker blockChangeTracker;
    
    public Arena(final String name, final Location firstTeamLocation, final Location secondTeamLocation) {
        this.open = true;
        this.blockChangeTracker = new BlockChangeTracker();
        this.enabled = false;
        this.name = name;
        this.firstTeamLocation = firstTeamLocation;
        this.secondTeamLocation = secondTeamLocation;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Location getFirstTeamLocation() {
        return this.firstTeamLocation;
    }
    
    public Location getSecondTeamLocation() {
        return this.secondTeamLocation;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setFirstTeamLocation(final Location firstTeamLocation) {
        this.firstTeamLocation = firstTeamLocation;
    }
    
    public void setSecondTeamLocation(final Location secondTeamLocation) {
        this.secondTeamLocation = secondTeamLocation;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public BlockChangeTracker getBlockChangeTracker() {
        return this.blockChangeTracker;
    }
}
