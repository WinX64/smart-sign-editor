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
package io.github.winx64.sse.tool.tools;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class PasteTool extends Tool {

    public PasteTool(SmartSignEditor plugin) {
	super(plugin, ToolType.PASTE, "Sign Paste", "Line Paste", Permissions.TOOL_PASTE_ALL,
		Permissions.TOOL_PASTE_LINE);
    }

    @Override
    public void usePrimary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (sPlayer.getTextBuffer() == null) {
	    player.sendMessage(ChatColor.RED + "You haven't copied any sign yet!");
	    return;
	}
	
	if (plugin.getVersionHandler().isSignBeingEdited(sign)
		&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
	    player.sendMessage(ChatColor.RED + "Someone is already editing this sign!");
	    return;
	}

	for (int i = 0; i < 4; i++) {
	    if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
		sign.setLine(i, sPlayer.getTextBuffer()[i]);
	    } else {
		sign.setLine(i, ChatColor.stripColor(sPlayer.getTextBuffer()[i]));
	    }
	}
	sign.update();
	player.sendMessage(ChatColor.GREEN + "Sign text replaced!");
    }

    @Override
    public void useSecondary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (sPlayer.getLineBuffer() == null) {
	    player.sendMessage(ChatColor.RED + "You haven't copied any line yet!");
	    return;
	}
	
	if (plugin.getVersionHandler().isSignBeingEdited(sign)
		&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
	    player.sendMessage(ChatColor.RED + "Someone is already editing this sign!");
	    return;
	}

	Vector intersection = MathUtil.getSightSignIntersection(player, sign);
	if (intersection == null) {
	    player.sendMessage(ChatColor.RED + "Choose a valid line, inside the sign plate!");
	    return;
	}
	int clickedLine = MathUtil.getSignLine(intersection, sign);

	if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
	    sign.setLine(clickedLine, sPlayer.getLineBuffer());
	} else {
	    sign.setLine(clickedLine, ChatColor.stripColor(sPlayer.getLineBuffer()));
	}
	sign.update();
	player.sendMessage(ChatColor.GREEN + "Line text pasted!");
    }

    @Override
    public boolean preSpecialHandling() {
	return false;
    }
}
