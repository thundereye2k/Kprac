package us.centile.practice.commands;

import us.centile.practice.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import us.centile.practice.util.*;
import org.bukkit.*;
import us.centile.practice.player.*;

public class BuilderCommand implements CommandExecutor
{
    private nPractice plugin;
    
    public BuilderCommand(final nPractice plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (!player.hasPermission("practice.commands.builder")) {
            player.sendMessage(Messages.NO_PERMISSION);
            return true;
        }
        final PracticePlayer practicePlayer = this.plugin.getManagerHandler().getPracticePlayerManager().getPracticePlayer(player);
        if (practicePlayer.getCurrentState() == PlayerState.BUILDER) {
            this.plugin.getManagerHandler().getPracticePlayerManager().sendToLobby(player);
            player.sendMessage(ChatColor.RED + "Builder mode has been deactivated");
            return true;
        }
        if (practicePlayer.getCurrentState() != PlayerState.LOBBY) {
            player.sendMessage(Messages.CANNOT_PERFORM_COMMAND_IN_CURRENT_STATE);
            return true;
        }
        practicePlayer.setCurrentState(PlayerState.BUILDER);
        player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(ChatColor.GREEN + "Builder mode has been activated");
        return true;
    }
}
