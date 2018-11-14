package us.centile.practice.duel;

public class DuelRequest
{
    private String kitName;
    
    public DuelRequest(final String kitName) {
        this.kitName = kitName;
    }
    
    public String getKitName() {
        return this.kitName;
    }
}
