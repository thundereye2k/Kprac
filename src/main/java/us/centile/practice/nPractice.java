package us.centile.practice;

import org.bukkit.plugin.java.*;
import us.centile.practice.manager.*;
import us.centile.practice.util.database.*;
import java.util.regex.*;
import org.bukkit.*;
import us.centile.practice.scoreboard.*;
import us.centile.practice.util.*;
import org.bukkit.event.*;
import us.centile.practice.listeners.*;
import us.centile.practice.tournament.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import us.centile.practice.commands.*;
import us.centile.practice.settings.*;

public class nPractice extends JavaPlugin
{
    private static nPractice instance;
    private ManagerHandler managerHandler;
    private Location spawn;
    PracticeDatabase practiceDatabase;
    public static Pattern splitPattern;
    public static final Pattern UUID_PATTER;
    public static String MAIN_COLOR;
    public static String SIDE_COLOR;
    public static String EXTRA_COLOR;
    
    public void onEnable() {
        nPractice.instance = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        SidebarProvider.SCOREBOARD_TITLE = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("SCOREBOARD_TITLE"));
        this.practiceDatabase = new PracticeDatabase(this);
        this.managerHandler = new ManagerHandler(this);
        if (this.getConfig().contains("spawn")) {
            this.spawn = LocationSerializer.deserializeLocation(this.getConfig().getString("spawn"));
        }
        try {
            nPractice.MAIN_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.MAIN_COLOR").toUpperCase()).toString();
            nPractice.SIDE_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.SIDE_COLOR").toUpperCase()).toString();
            nPractice.EXTRA_COLOR = ChatColor.valueOf(this.getConfig().getString("COLORS.EXTRA_COLOR").toUpperCase()).toString();
        }
        catch (Exception e) {
            System.out.print(e.getClass() + ": " + e.getMessage());
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
            System.out.print("Oops your config colors are invalid, colors has been set to default.");
        }
        this.registerListeners();
        this.registerCommands();
        nPractice.splitPattern = Pattern.compile("\\s");
    }
    
    public void onDisable() {
        this.reloadConfig();
        this.managerHandler.disable();
    }
    
    private void registerListeners() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new HitDetectionListener(this), this);
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new EntityListener(this), this);
        pm.registerEvents(new DuelListener(this), this);
        pm.registerEvents(new BlockListener(this), this);
        pm.registerEvents(new TournamentListener(), this);
    }
    
    private void registerCommands() {
        this.getCommand("arena").setExecutor(new ArenaCommand(this));
        this.getCommand("duel").setExecutor(new DuelCommand(this));
        this.getCommand("accept").setExecutor(new AcceptCommand(this));
        this.getCommand("kit").setExecutor(new KitCommand(this));
        this.getCommand("spectate").setExecutor(new SpectateCommand(this));
        this.getCommand("builder").setExecutor(new BuilderCommand(this));
        this.getCommand("inventory").setExecutor(new InventoryCommand(this));
        this.getCommand("party").setExecutor(new PartyCommand(this));
        this.getCommand("reset").setExecutor(new ResetEloCommand(this));
        this.getCommand("join").setExecutor(new JoinCommand(this));
        this.getCommand("leave").setExecutor(new LeaveCommand(this));
        this.getCommand("credits").setExecutor(new CreditsCommand(this));
        this.getCommand("tournament").setExecutor(new TournamentCommand(this));
        this.getCommand("scoreboard").setExecutor(new ScoreboardCommand(this));
        this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        this.getCommand("tokens").setExecutor(new PremiumTokensCommand(this));
        this.getCommand("matches").setExecutor(new MatchesCommand(this));
        this.getCommand("host").setExecutor(new HostCommand(this));
        this.getCommand("ping").setExecutor(new PingCommand(this));
        this.getCommand("premiumtop").setExecutor(new SortPremiumEloCommand(this));
        this.getCommand("setting").setExecutor(new SettingsHandler(this));
    }
    
    public ManagerHandler getManagerHandler() {
        return this.managerHandler;
    }
    
    public static nPractice getInstance() {
        return nPractice.instance;
    }
    
    public PracticeDatabase getPracticeDatabase() {
        return this.practiceDatabase;
    }
    
    public Location getSpawn() {
        return this.spawn;
    }
    
    public void setSpawn(final Location spawn) {
        this.spawn = spawn;
    }
    
    static {
        nPractice.MAIN_COLOR = ChatColor.DARK_GREEN.toString();
        nPractice.SIDE_COLOR = ChatColor.GREEN.toString();
        nPractice.EXTRA_COLOR = ChatColor.GRAY.toString();
        UUID_PATTER = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    }
}
