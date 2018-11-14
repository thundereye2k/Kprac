package us.centile.practice.player;

import us.centile.practice.*;
import us.centile.practice.settings.*;
import com.google.common.collect.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bson.*;
import com.google.gson.*;
import com.mongodb.client.model.*;
import org.json.simple.*;
import java.net.*;
import java.io.*;
import org.json.simple.parser.*;
import java.util.*;

public class PracticePlayer
{
    private static Set<PracticePlayer> profiles;
    public static nPractice main;
    private UUID uuid;
    private String name;
    private PlayerState currentState;
    private int teamNumber;
    private int rankPremiumTokens;
    private int premiumTokens;
    private int premiumWins;
    private int premiumLosses;
    private int rankedWins;
    private int rankedLosses;
    private int unrankedWins;
    private int unrankedLosses;
    private int globalPersonalElo;
    private int globalPartyElo;
    private int globalPremiumElo;
    private int credits;
    private boolean scoreboard;
    private long hostCooldown;
    private List<Match> matches;
    private Map<String, Map<Integer, PlayerKit>> playerKitMap;
    private Map<String, Integer> playerEloMap;
    private Map<String, Integer> premiumEloMap;
    private Map<UUID, Map<String, Integer>> partyEloMap;
    private Settings settings;
    private transient LinkedList<Projectile> projectileQueue;
    private transient boolean showRematchItemFlag;
    private transient String lastDuelPlayer;
    
    public PracticePlayer(final UUID uuid, final boolean cache) {
        this.settings = new Settings();
        this.projectileQueue = Lists.newLinkedList();
        this.playerEloMap = new HashMap<String, Integer>();
        this.playerKitMap = new HashMap<String, Map<Integer, PlayerKit>>();
        this.premiumEloMap = new HashMap<String, Integer>();
        this.partyEloMap = new HashMap<UUID, Map<String, Integer>>();
        this.matches = new ArrayList<Match>();
        this.uuid = uuid;
        this.currentState = PlayerState.LOBBY;
        this.premiumTokens = 0;
        this.rankPremiumTokens = 0;
        this.rankedWins = 0;
        this.unrankedWins = 0;
        this.premiumWins = 0;
        this.rankedLosses = 0;
        this.unrankedLosses = 0;
        this.premiumLosses = 0;
        this.credits = 0;
        this.globalPartyElo = 0;
        this.globalPersonalElo = 0;
        this.globalPremiumElo = 0;
        this.hostCooldown = 0L;
        this.scoreboard = true;
        final Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.name = player.getName();
        }
        else {
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null) {
                this.name = offlinePlayer.getName();
            }
        }
        this.load();
        if (cache) {
            PracticePlayer.profiles.add(this);
        }
    }
    
    public int getCredits() {
        return this.credits;
    }
    
    public long getHostCooldown() {
        return this.hostCooldown;
    }
    
    public void setHostCooldown(final long hostCooldown) {
        this.hostCooldown = hostCooldown;
    }
    
    public boolean isScoreboard() {
        return this.scoreboard;
    }
    
    public void setScoreboard(final boolean scoreboard) {
        this.scoreboard = scoreboard;
    }
    
    public void setCredits(final int credits) {
        this.credits = credits;
    }
    
    public int getTeamNumber() {
        return this.teamNumber;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public PlayerState getCurrentState() {
        return this.currentState;
    }
    
    public void setTeamNumber(final int i) {
        this.teamNumber = i;
    }
    
    public void addElo(final String kitName, final int elo) {
        this.playerEloMap.put(kitName, elo);
    }
    
    public void addPremiumElo(final String kitName, final int elo) {
        this.premiumEloMap.put(kitName, elo);
    }
    
    public void addPartyElo(final UUID uuid, final String kitName, final int elo) {
        if (!this.partyEloMap.containsKey(uuid)) {
            this.partyEloMap.put(uuid, new HashMap<String, Integer>());
        }
        this.partyEloMap.get(uuid).put(kitName, elo);
    }
    
    public void addKit(final String kitName, final Integer kitIndex, final PlayerKit playerKit) {
        if (!this.playerKitMap.containsKey(kitName)) {
            this.playerKitMap.put(kitName, new HashMap<Integer, PlayerKit>());
        }
        this.playerKitMap.get(kitName).put(kitIndex, playerKit);
    }
    
    public void setCurrentState(final PlayerState playerState) {
        this.currentState = playerState;
    }
    
    public Map<String, Integer> getEloMap() {
        return this.playerEloMap;
    }
    
    public List<Match> getMatches() {
        return this.matches;
    }
    
    public Map<String, Integer> getPremiumEloMap() {
        return this.premiumEloMap;
    }
    
    public Map<String, Map<Integer, PlayerKit>> getKitMap() {
        return this.playerKitMap;
    }
    
    public Map<UUID, Map<String, Integer>> getPartyEloMap() {
        return this.partyEloMap;
    }
    
    public void load() {
        final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("uuid", this.uuid.toString())).first();
        if (document != null) {
            for (final JsonElement element : new JsonParser().parse(document.getString("player_elo")).getAsJsonArray()) {
                final JsonObject practiceDocument = element.getAsJsonObject();
                if (practiceDocument.has("kit_personal_name")) {
                    this.addElo(practiceDocument.get("kit_personal_name").getAsString(), practiceDocument.get("kit_personal_elo").getAsInt());
                }
                if (practiceDocument.has("kit_premium_name")) {
                    this.addPremiumElo(practiceDocument.get("kit_premium_name").getAsString(), practiceDocument.get("kit_premium_elo").getAsInt());
                }
                if (practiceDocument.has("kit_party_uuid")) {
                    this.addPartyElo(UUID.fromString(practiceDocument.get("kit_party_uuid").getAsString()), practiceDocument.get("kit_party_name").getAsString(), practiceDocument.get("kit_party_elo").getAsInt());
                }
            }
            if (document.containsKey("rankedWins")) {
                this.rankedWins = document.getInteger("rankedWins");
            }
            if (document.containsKey("rankedLosses")) {
                this.rankedLosses = document.getInteger("rankedLosses");
            }
            if (document.containsKey("unrankedWins")) {
                this.unrankedWins = document.getInteger("unrankedWins");
            }
            if (document.containsKey("unrankedLosses")) {
                this.unrankedLosses = document.getInteger("unrankedLosses");
            }
            if (document.containsKey("premiumWins")) {
                this.premiumWins = document.getInteger("premiumWins");
            }
            if (document.containsKey("premiumLosses")) {
                this.premiumLosses = document.getInteger("premiumLosses");
            }
            if (document.containsKey("hostCooldown")) {
                this.hostCooldown = document.getLong("hostCooldown");
            }
            if (document.containsKey("globalPersonalElo")) {
                this.globalPersonalElo = document.getInteger("globalPersonalElo");
            }
            if (document.containsKey("globalPartyElo")) {
                this.globalPartyElo = document.getInteger("globalPartyElo");
            }
            if (document.containsKey("globalPremiumElo")) {
                this.globalPremiumElo = document.getInteger("globalPremiumElo");
            }
            if (document.containsKey("premiumTokens")) {
                this.premiumTokens = document.getInteger("premiumTokens");
            }
            if (document.containsKey("rankPremiumTokens")) {
                this.rankPremiumTokens = document.getInteger("rankPremiumTokens");
            }
            if (document.containsKey("credits")) {
                this.credits = document.getInteger("credits");
            }
            if (document.containsKey("scoreboard")) {
                this.scoreboard = document.getBoolean("scoreboard");
            }
            if (document.containsKey("recentName")) {
                this.name = document.getString("recentName");
            }
        }
    }
    
    public void save() {
        final Document document = new Document();
        final JsonArray eloDocument = new JsonArray();
        final JsonArray matchesDocument = new JsonArray();
        for (final Map.Entry<String, Integer> entry : this.playerEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_personal_name", entry.getKey());
            practiceDocument.addProperty("kit_personal_elo", entry.getValue());
            eloDocument.add(practiceDocument);
        }
        for (final Map.Entry<String, Integer> entry : this.premiumEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_premium_name", entry.getKey());
            practiceDocument.addProperty("kit_premium_elo", entry.getValue());
            eloDocument.add(practiceDocument);
        }
        for (final Map.Entry<UUID, Map<String, Integer>> entry2 : this.partyEloMap.entrySet()) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("kit_party_uuid", entry2.getKey().toString());
            for (final Map.Entry<String, Integer> sideEntry : entry2.getValue().entrySet()) {
                practiceDocument.addProperty("kit_party_name", sideEntry.getKey());
                practiceDocument.addProperty("kit_party_elo", sideEntry.getValue());
            }
            eloDocument.add(practiceDocument);
        }
        for (final Match match : this.matches) {
            final JsonObject practiceDocument = new JsonObject();
            practiceDocument.addProperty("matchId", match.getMatchId().toString());
            practiceDocument.addProperty("items", match.getItems());
            practiceDocument.addProperty("armor", match.getArmor());
            practiceDocument.addProperty("potions", match.getPotions());
            practiceDocument.addProperty("eloChangeLoser", match.getEloChangeLoser());
            practiceDocument.addProperty("eloChangeWinner", match.getEloChangeWinner());
            practiceDocument.addProperty("winningTeamId", match.getWinningTeamId());
            practiceDocument.addProperty("firstTeam", match.getFirstTeam());
            practiceDocument.addProperty("secondTeam", match.getSecondTeam());
            matchesDocument.add(practiceDocument);
        }
        document.put("uuid", this.uuid.toString());
        document.put("credits", this.credits);
        document.put("scoreboard", this.scoreboard);
        document.put("premiumTokens", this.premiumTokens);
        document.put("rankPremiumTokens", this.rankPremiumTokens);
        document.put("rankedWins", this.rankedWins);
        document.put("rankedLosses", this.rankedLosses);
        document.put("unrankedWins", this.unrankedWins);
        document.put("unrankedLosses", this.unrankedLosses);
        document.put("premiumWins", this.premiumWins);
        document.put("premiumLosses", this.premiumLosses);
        document.put("globalPersonalElo", this.globalPersonalElo);
        document.put("globalPartyElo", this.globalPartyElo);
        document.put("globalPremiumElo", this.globalPremiumElo);
        document.put("hostCooldown", this.hostCooldown);
        if (this.name != null) {
            document.put("recentName", this.name);
            document.put("recentNameLowercase", this.name.toLowerCase());
            document.put("recentNameLength", this.name.length());
        }
        document.put("player_elo", eloDocument.toString());
        document.put("matches", matchesDocument.toString());
        PracticePlayer.main.getPracticeDatabase().getProfiles().replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new UpdateOptions().upsert(true));
    }
    
    public static PracticePlayer getByUuid(final UUID uuid) {
        for (final PracticePlayer profile : PracticePlayer.profiles) {
            if (profile.getUUID().equals(uuid)) {
                return profile;
            }
        }
        return getExternalByUuid(uuid);
    }
    
    private static PracticePlayer getExternalByUuid(final UUID uuid) {
        final PracticePlayer profile = new PracticePlayer(uuid, false);
        return profile;
    }
    
    public static Map.Entry<UUID, String> getExternalPlayerInformation(String name) throws IOException, ParseException {
        final Document document = (Document)PracticePlayer.main.getPracticeDatabase().getProfiles().find(Filters.eq("recentName", name)).first();
        if (document != null && document.containsKey("recentName")) {
            return new AbstractMap.SimpleEntry<UUID, String>(UUID.fromString(document.getString("uuid")), document.getString("recentName"));
        }
        final URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        final URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        final JSONParser parser = new JSONParser();
        final JSONObject obj = (JSONObject)parser.parse(reader.readLine());
        final UUID uuid = UUID.fromString(String.valueOf(obj.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        name = String.valueOf(obj.get("name"));
        reader.close();
        return new AbstractMap.SimpleEntry<UUID, String>(uuid, name);
    }
    
    public static Set<PracticePlayer> getProfiles() {
        return PracticePlayer.profiles;
    }
    
    public int getRankPremiumTokens() {
        return this.rankPremiumTokens;
    }
    
    public void setRankPremiumTokens(final int rankPremiumTokens) {
        this.rankPremiumTokens = rankPremiumTokens;
    }
    
    public int getPremiumTokens() {
        return this.premiumTokens;
    }
    
    public void setPremiumTokens(final int premiumTokens) {
        this.premiumTokens = premiumTokens;
    }
    
    public int getPremiumWins() {
        return this.premiumWins;
    }
    
    public void setPremiumWins(final int premiumWins) {
        this.premiumWins = premiumWins;
    }
    
    public int getPremiumLosses() {
        return this.premiumLosses;
    }
    
    public void setPremiumLosses(final int premiumLosses) {
        this.premiumLosses = premiumLosses;
    }
    
    public int getRankedWins() {
        return this.rankedWins;
    }
    
    public void setRankedWins(final int rankedWins) {
        this.rankedWins = rankedWins;
    }
    
    public int getRankedLosses() {
        return this.rankedLosses;
    }
    
    public void setRankedLosses(final int rankedLosses) {
        this.rankedLosses = rankedLosses;
    }
    
    public int getUnrankedWins() {
        return this.unrankedWins;
    }
    
    public void setUnrankedWins(final int unrankedWins) {
        this.unrankedWins = unrankedWins;
    }
    
    public int getUnrankedLosses() {
        return this.unrankedLosses;
    }
    
    public void setUnrankedLosses(final int unrankedLosses) {
        this.unrankedLosses = unrankedLosses;
    }
    
    public int getGlobalPersonalElo() {
        return this.globalPersonalElo;
    }
    
    public void setGlobalPersonalElo(final int globalPersonalElo) {
        this.globalPersonalElo = globalPersonalElo;
    }
    
    public int getGlobalPartyElo() {
        return this.globalPartyElo;
    }
    
    public void setGlobalPartyElo(final int globalPartyElo) {
        this.globalPartyElo = globalPartyElo;
    }
    
    public int getGlobalPremiumElo() {
        return this.globalPremiumElo;
    }
    
    public void setGlobalPremiumElo(final int globalPremiumElo) {
        this.globalPremiumElo = globalPremiumElo;
    }
    
    public Settings getSettings() {
        return this.settings;
    }
    
    public void setSettings(final Settings settings) {
        this.settings = settings;
    }
    
    public LinkedList<Projectile> getProjectileQueue() {
        return this.projectileQueue;
    }
    
    public boolean isShowRematchItemFlag() {
        return this.showRematchItemFlag;
    }
    
    public void setShowRematchItemFlag(final boolean showRematchItemFlag) {
        this.showRematchItemFlag = showRematchItemFlag;
    }
    
    public String getLastDuelPlayer() {
        return this.lastDuelPlayer;
    }
    
    public void setLastDuelPlayer(final String lastDuelPlayer) {
        this.lastDuelPlayer = lastDuelPlayer;
    }
    
    static {
        PracticePlayer.profiles = new HashSet<PracticePlayer>();
        PracticePlayer.main = nPractice.getInstance();
    }
}
