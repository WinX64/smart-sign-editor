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
import org.bukkit.util.Vector;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages.Message;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class EraseTool extends Tool {

	public EraseTool(SmartSignEditor plugin) {
		super(plugin, ToolType.ERASE, "Sign Erase", "Line Erase", Permissions.TOOL_ERASE_ALL,
				Permissions.TOOL_ERASE_LINE, true, false);
	}

	@Override
	public void usePrimary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		if (plugin.getVersionAdapter().isSignBeingEdited(sign)
				&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
			player.sendMessage(signMessages.get(Message.OVERRIDE_NO_PERMISSION));
			return;
		}

		for (int i = 0; i < 4; i++) {
			sign.setLine(i, "");
		}
		sign.update();
		player.sendMessage(signMessages.get(Message.TOOL_SIGN_CLEARED));
		this.primaryUseCount++;
	}

	@Override
	public void useSecondary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		if (plugin.getVersionAdapter().isSignBeingEdited(sign)
				&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
			player.sendMessage(signMessages.get(Message.OVERRIDE_NO_PERMISSION));
			return;
		}

		Vector intersection = MathUtil.getSightSignIntersection(player, sign);
		if (intersection == null) {
			player.sendMessage(signMessages.get(Message.INVALID_LINE));
			return;
		}

		int clickedLine = MathUtil.getSignLine(intersection, sign);

		sign.setLine(clickedLine, "");
		sign.update();
		player.sendMessage(signMessages.get(Message.TOOL_LINE_CLEARED));
		this.secondaryUseCount++;
	}
}
