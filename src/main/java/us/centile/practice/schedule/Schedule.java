package us.centile.practice.schedule;

import org.bukkit.*;
import us.centile.practice.*;
import org.bson.*;
import com.mongodb.client.model.*;
import us.centile.practice.player.*;
import com.mongodb.client.*;
import java.util.*;

public class Schedule
{
    private long eventTime;
    private long announceTime;
    
    public Schedule(final long eventTime) {
        this.eventTime = eventTime;
        this.announceTime = this.eventTime - 1800000L;
    }
    
    public void runEvent() {
        if (System.currentTimeMillis() > this.eventTime) {
            try {
                this.clearPremiumMatches();
                this.setNextEventTime();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (System.currentTimeMillis() > this.announceTime) {
            Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Premium Matches Tokens will clear in 30 minutes.");
            this.setNextAnnounceTime();
        }
    }
    
    public long getEventTime() {
        return this.eventTime;
    }
    
    private void setNextEventTime() {
        this.eventTime += 604800000L;
    }
    
    private void setNextAnnounceTime() {
        this.announceTime += 604800000L;
    }
    
    private void clearPremiumMatches() {
        final FindIterable<Document> iterable = (FindIterable<Document>)nPractice.getInstance().getPracticeDatabase().getProfiles().find();
        for (final Document document : iterable) {
            final UUID uuid = UUID.fromString(document.getString((Object)"uuid"));
            final int rankTokens = document.getInteger((Object)"rankPremiumTokens");
            document.put("premiumTokens", (Object)rankTokens);
            nPractice.getInstance().getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", (Object)uuid.toString()), (Object)document, new UpdateOptions().upsert(true));
        }
        for (final PracticePlayer practicePlayer : PracticePlayer.getProfiles()) {
            practicePlayer.load();
        }
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
        Bukkit.getServer().broadcastMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Premium Matches have been reset");
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
    }
}
