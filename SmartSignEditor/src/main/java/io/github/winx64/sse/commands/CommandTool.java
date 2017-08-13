/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2017
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
