package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.util.*;
import java.util.*;
import us.centile.practice.player.*;

public class InventoryCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public InventoryCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        final Player player = (Player)sender;
        if (!args[0].matches(nPractice.UUID_PATTER.pattern())) {
            player.sendMessage(Messages.CANNOT_FIND_SNAPSHOT);
            return true;
        }
        final UUID invUUID = UUID.fromString(args[0]);
        final PlayerInventorySnapshot playerInventorySnapshot = this.plugin.getManagerHandler().getInventorySnapshotManager().getSnapshotFromUUID(invUUID);
        if (playerInventorySnapshot == null) {
            player.sendMessage(Messages.CANNOT_FIND_SNAPSHOT);
            return true;
        }
        player.openInventory(playerInventorySnapshot.getInventory());
        return true;
    }
}
