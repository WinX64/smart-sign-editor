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
package io.github.winx64.sse.tool.tools;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages.Message;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class ToolChange extends Tool {

	public ToolChange(SmartSignEditor plugin) {
		super(plugin, null, "Next Tool", "Previous Tool", null, null, false);
	}

	@Override
	public void usePrimary(SmartPlayer sPlayer, Sign sign) {
		changeTool(sPlayer, true);
	}

	@Override
	public void useSecondary(SmartPlayer sPlayer, Sign sign) {
		changeTool(sPlayer, false);
	}

	@Override
	public boolean preSpecialHandling() {
		return true;
	}

	private void changeTool(SmartPlayer sPlayer, boolean foward) {
		Player player = sPlayer.getPlayer();
		ToolType currentTool = sPlayer.getToolType();
		ToolType newTool = currentTool;
		while (true) {
			newTool = foward ? newTool.getNextToolMode() : newTool.getPreviousToolMode();
			if (newTool == currentTool || player.hasPermission(newTool.getPermission())) {
				break;
			}
		}

		if (!player.hasPermission(newTool.getPermission())) {
			return;
		}

		sPlayer.setToolMode(newTool);
		sPlayer.getPlayer().sendMessage(signMessages.get(Message.TOOL_CHANGED, newTool.getName()));
	}
}
