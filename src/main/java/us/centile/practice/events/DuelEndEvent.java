package us.centile.practice.events;

import org.bukkit.event.*;
import us.centile.practice.duel.*;

public class DuelEndEvent extends Event
{
    private static HandlerList handlerList;
    private Duel duel;
    
    public DuelEndEvent(final Duel duel) {
        this.duel = duel;
    }
    
    public Duel getDuel() {
        return this.duel;
    }
    
    public HandlerList getHandlers() {
        return DuelEndEvent.handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return DuelEndEvent.handlerList;
    }
    
    static {
        DuelEndEvent.handlerList = new HandlerList();
    }
}
