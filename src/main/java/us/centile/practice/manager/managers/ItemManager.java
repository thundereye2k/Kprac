package us.centile.practice.manager.managers;

import org.bukkit.inventory.*;
import us.centile.practice.manager.*;
import org.bukkit.*;
import us.centile.practice.util.*;
import org.bukkit.enchantments.*;

public class ItemManager extends Manager
{
    private ItemStack[] spawnItems;
    private ItemStack[] queueItems;
    private ItemStack[] specItems;
    private ItemStack[] partyItems;
    private ItemStack[] spectatorModeItems;
    
    public ItemManager(final ManagerHandler handler) {
        super(handler);
        this.loadSpawnItems();
        this.loadQueueItems();
        this.loadPartyItems();
        this.loadSpecItems();
        this.loadSpectatorModeItems();
    }
    
    private void loadSpawnItems() {
        this.spawnItems = new ItemStack[] { UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, ChatColor.DARK_GREEN + "Unranked Queue"), UtilItem.createItem(Material.DIAMOND_SWORD, 1, (short)0, ChatColor.GREEN + "Ranked Queue"), UtilItem.createItem(Material.GOLD_SWORD, 1, (short)0, ChatColor.RED.toString() + ChatColor.BOLD + "Premium Queue", Enchantment.DURABILITY, 1), null, UtilItem.createItem(Material.NAME_TAG, 1, (short)0, ChatColor.AQUA + "Create Party"), null, UtilItem.createItem(Material.WATCH, 1, (short)0, ChatColor.AQUA + "Settings"), UtilItem.createItem(Material.BOOK, 1, (short)0, ChatColor.GOLD + "Edit Kits"), UtilItem.createItem(Material.COMPASS, 1, (short)0, ChatColor.YELLOW + "Spectator Mode") };
    }
    
    private void loadPartyItems() {
        this.partyItems = new ItemStack[] { UtilItem.createItem(Material.IRON_SWORD, 1, (short)0, ChatColor.DARK_GREEN + "Unranked 2v2 Queue"), UtilItem.createItem(Material.DIAMOND_SWORD, 1, (short)0, ChatColor.GREEN + "Ranked 2v2 Queue"), null, UtilItem.createItem(Material.PAPER, 1, (short)0, ChatColor.YELLOW + "Fight Other Parties"), UtilItem.createItem(Material.SKULL_ITEM, 1, (short)0, ChatColor.GOLD + "Party Information"), UtilItem.createItem(Material.BOOK, 1, (short)0, ChatColor.GOLD + "Edit Kits"), null, UtilItem.createItem(Material.GOLD_AXE, 1, (short)0, ChatColor.GREEN + "Party Events"), UtilItem.createItem(Material.NETHER_STAR, 1, (short)0, ChatColor.RED + "Leave Party") };
    }
    
    private void loadSpecItems() {
        this.specItems = new ItemStack[] { UtilItem.createItem(Material.COMPASS, 1, (short)0, ChatColor.GREEN + "Inspect Inventory"), null, null, null, null, null, null, null, UtilItem.createItem(Material.REDSTONE, 1, (short)0, ChatColor.RED + "Return to Lobby") };
    }
    
    private void loadSpectatorModeItems() {
        this.spectatorModeItems = new ItemStack[] { null, null, null, UtilItem.createItem(Material.EMERALD, 1, (short)0, ChatColor.GOLD + "Spectate Match (Menu)"), UtilItem.createItem(Material.PAPER, 1, (short)0, ChatColor.YELLOW + "Spectate Random Match"), UtilItem.createItem(Material.NETHER_STAR, 1, (short)0, ChatColor.RED + "Leave Spectator Mode"), null, null, null };
    }
    
    private void loadQueueItems() {
        this.queueItems = new ItemStack[] { UtilItem.createItem(Material.REDSTONE, 1, (short)0, ChatColor.YELLOW + "Leave Queue"), null, null, null, null, null, null, null, null };
    }
    
    public ItemStack[] getSpawnItems() {
        return this.spawnItems;
    }
    
    public ItemStack[] getQueueItems() {
        return this.queueItems;
    }
    
    public ItemStack[] getSpecItems() {
        return this.specItems;
    }
    
    public ItemStack[] getPartyItems() {
        return this.partyItems;
    }
    
    public ItemStack[] getSpectatorModeItems() {
        return this.spectatorModeItems;
    }
}
