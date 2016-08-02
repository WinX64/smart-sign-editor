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
package io.github.winx64.sse.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.Permissions;

public class SignChangeListener implements Listener {

    @SuppressWarnings("unused")
    private SmartSignEditor plugin;

    public SignChangeListener(SmartSignEditor plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
	Player player = event.getPlayer();

	if (player.hasPermission(Permissions.TOOL_EDIT_COLORS)) {
	    for (int i = 0; i < 4; i++) {
		event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
	    }
	}
    }
}
