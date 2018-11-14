package us.centile.practice.util;

import org.bukkit.*;

public class Messages
{
    public static final String PLAYER_NOT_FOUND;
    public static final String PLAYER_BUSY;
    public static final String WAIT_SENDING_DUEL;
    public static final String NO_REQUESTS_FOUND;
    public static final String WAIT_EPEARL;
    public static final String REQUESTED_PLAYER_NOT_IN_FIGHT;
    public static final String NO_PERMISSION;
    public static final String CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE;
    public static final String CANNOT_FIND_SNAPSHOT;
    public static final String CANNOT_DUEL_YOURSELF;
    public static final String SPECIFY_KIT;
    
    static {
        PLAYER_NOT_FOUND = ChatColor.RED + "Player not found";
        PLAYER_BUSY = ChatColor.RED + "This player is currently busy";
        WAIT_SENDING_DUEL = ChatColor.RED + "You already sent a duel request to this player, you must wait before sending one again.";
        NO_REQUESTS_FOUND = ChatColor.RED + "You do not have any pending requests";
        WAIT_EPEARL = ChatColor.RED + "You must wait before throwing another ender pearl";
        REQUESTED_PLAYER_NOT_IN_FIGHT = ChatColor.RED + "The requested player is currently not in a fight";
        NO_PERMISSION = ChatColor.RED + "You do not have sufficient permissions to use this command";
        CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE = ChatColor.RED + "Cannot execute this command in your current state";
        CANNOT_FIND_SNAPSHOT = ChatColor.RED + "Cannot find the requested inventory. Maybe it expired?";
        CANNOT_DUEL_YOURSELF = ChatColor.RED + "Cannot duel yourself!";
        SPECIFY_KIT = ChatColor.RED + "Please Specify Kit";
    }
}
