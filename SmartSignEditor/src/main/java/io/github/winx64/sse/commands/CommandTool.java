package io.github.winx64.sse.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages;
import io.github.winx64.sse.configuration.SignMessages.Message;

public final class CommandTool implements CommandExecutor {

	private final SmartSignEditor plugin;
	private final SignMessages signMessages;

	public CommandTool(SmartSignEditor plugin) {
		this.plugin = plugin;
		this.signMessages = plugin.getSignMessages();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(signMessages.get(Message.COMMAND_NO_CONSOLE));
			return true;
		}

		Player player = (Player) sender;
		player.getInventory().addItem(plugin.getSignConfig().getToolItem());
		return true;
	}

}
