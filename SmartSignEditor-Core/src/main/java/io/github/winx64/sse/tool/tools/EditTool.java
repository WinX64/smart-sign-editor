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

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.SignMessages.Message;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class EditTool extends Tool {

    public EditTool(SmartSignEditor plugin) {
	super(plugin, ToolType.EDIT, null, null, null, null);
    }

    @Override
    public void usePrimary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (plugin.getVersionAdapter().isSignBeingEdited(sign)
		&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
	    player.sendMessage(signMessages.get(Message.OVERRIDE_NO_PERMISSION));
	    return;
	}

	String[] noColors = sign.getLines();
	for (int i = 0; i < 4; i++) {
	    noColors[i] = noColors[i].replace(ChatColor.COLOR_CHAR, '&');
	}
	plugin.getVersionAdapter().updateSignText(player, sign, noColors);
	plugin.getVersionAdapter().openSignEditor(player, sign);
    }

    @Override
    public void useSecondary(SmartPlayer sPlayer, Sign sign) {
	this.usePrimary(sPlayer, sign);
    }

    @Override
    public boolean preSpecialHandling() {
	return true;
    }
}
