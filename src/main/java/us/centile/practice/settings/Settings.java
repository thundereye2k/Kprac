package us.centile.practice.settings;

public class Settings
{
    private boolean scoreboard;
    private boolean duelRequests;
    private boolean publicChat;
    
    public Settings() {
        this.scoreboard = true;
        this.duelRequests = true;
        this.publicChat = true;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public boolean isDuelRequests() {
        return this.duelRequests;
    }
    
    public boolean isPublicChat() {
        return this.publicChat;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public void setDuelRequests(final boolean duelRequests) {
        this.duelRequests = duelRequests;
    }
    
    public void setPublicChat(final boolean publicChat) {
        this.publicChat = publicChat;
    }
}
