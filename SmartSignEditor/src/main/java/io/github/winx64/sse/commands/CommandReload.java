package io.github.winx64.sse.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages;
import io.github.winx64.sse.configuration.SignMessages.Message;

public final class CommandReload implements CommandExecutor {

	private final SmartSignEditor plugin;
	private final SignMessages signMessages;

	public CommandReload(SmartSignEditor plugin) {
		this.plugin = plugin;
		this.signMessages = plugin.getSignMessages();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (plugin.getSignConfig().loadConfiguration() && plugin.getSignMessages().loadMessages()) {
			sender.sendMessage(signMessages.get(Message.COMMAND_RELOAD_SUCCESS));
		} else {
			sender.sendMessage(signMessages.get(Message.COMMAND_RELOAD_FAILURE));
		}
		return true;
	}
}
