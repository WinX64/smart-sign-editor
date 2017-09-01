package io.github.winx64.sse.tool.tools;

import org.bukkit.ChatColor;
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

public final class PasteTool extends Tool {

	public PasteTool(SmartSignEditor plugin) {
		super(plugin, ToolType.PASTE, "Sign Paste", "Line Paste", Permissions.TOOL_PASTE_ALL,
				Permissions.TOOL_PASTE_LINE, true, false);
	}

	@Override
	public void usePrimary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		if (sPlayer.getSignBuffer() == null) {
			player.sendMessage(signMessages.get(Message.EMPTY_SIGN_BUFFER));
			return;
		}

		if (plugin.getVersionAdapter().isSignBeingEdited(sign)
				&& !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
			player.sendMessage(signMessages.get(Message.OVERRIDE_NO_PERMISSION));
			return;
		}

		for (int i = 0; i < 4; i++) {
			if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
				sign.setLine(i, sPlayer.getSignBuffer()[i]);
			} else {
				sign.setLine(i, ChatColor.stripColor(sPlayer.getSignBuffer()[i]));
			}
		}
		sign.update();
		player.sendMessage(signMessages.get(Message.TOOL_SIGN_REPLACED));
		this.primaryUseCount++;
	}

	@Override
	public void useSecondary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		if (sPlayer.getLineBuffer() == null) {
			player.sendMessage(signMessages.get(Message.EMPTY_LINE_BUFFER));
			return;
		}

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

		if (player.hasPermission(Permissions.TOOL_PASTE_COLORS)) {
			sign.setLine(clickedLine, sPlayer.getLineBuffer());
		} else {
			sign.setLine(clickedLine, ChatColor.stripColor(sPlayer.getLineBuffer()));
		}
		sign.update();
		player.sendMessage(signMessages.get(Message.TOOL_LINE_REPLACED, sPlayer.getLineBuffer()));
		this.secondaryUseCount++;
	}
}
