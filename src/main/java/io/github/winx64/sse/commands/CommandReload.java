package io.github.winx64.sse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.winx64.sse.SmartSignEditor;

public final class CommandReload implements CommandExecutor {

    private static final String LOAD_SUCCESS = ChatColor.GREEN + "Configuration reloaded with success!";
    private static final String LOAD_FAILURE = ChatColor.RED
	    + "Failed to reload the configuration! Check the console for more information!";

    private final SmartSignEditor plugin;

    public CommandReload(SmartSignEditor plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
	if (plugin.getSignConfig().loadConfiguration()) {
	    sender.sendMessage(LOAD_SUCCESS);
	} else {
	    sender.sendMessage(LOAD_FAILURE);
	}
	return true;
    }

}
