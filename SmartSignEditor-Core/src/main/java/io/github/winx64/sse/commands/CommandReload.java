/*
 *   SmartSignEditor - Edit your signs with style
 *   Copyright (C) WinX64 2013-2016
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

import io.github.winx64.sse.SignMessages;
import io.github.winx64.sse.SignMessages.Message;
import io.github.winx64.sse.SmartSignEditor;

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
