package us.centile.practice.events;

import org.bukkit.event.*;
import us.centile.practice.duel.*;

public class DuelCreateEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    public DuelCreateEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelCreateEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return DuelCreateEvent.handlerList;
    }
    
    static {
        DuelCreateEvent.handlerList = new HandlerList();
    }
}
