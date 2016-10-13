package io.github.winx64.sse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;

public final class CommandTool implements CommandExecutor {
    
    private final SmartSignEditor plugin;
    
    public CommandTool(SmartSignEditor plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
	if (!(sender instanceof Player)) {
	    sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
	    return true;
	}
	
	Player player = (Player)sender;
	player.getInventory().addItem(plugin.getSignConfig().getToolItem());
	return true;
    }

}
