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

public final class CopyTool extends Tool {

	public CopyTool(SmartSignEditor plugin) {
		super(plugin, ToolType.COPY, "Sign Copy", "Line Copy", Permissions.TOOL_COPY_ALL, Permissions.TOOL_COPY_LINE,
				false, false);
	}

	@Override
	public void usePrimary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		for (int i = 0; i < 4; i++) {
			if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
				sPlayer.setSignBuffer(i, ChatColor.stripColor(sign.getLine(i)));
			} else {
				sPlayer.setSignBuffer(i, sign.getLine(i));
			}
		}
		player.sendMessage(signMessages.get(Message.TOOL_SIGN_COPIED));
		this.primaryUseCount++;
	}

	@Override
	public void useSecondary(SmartPlayer sPlayer, Sign sign) {
		Player player = sPlayer.getPlayer();

		Vector intersection = MathUtil.getSightSignIntersection(player, sign);
		if (intersection == null) {
			player.sendMessage(signMessages.get(Message.INVALID_LINE));
			return;
		}
		int clickedLine = MathUtil.getSignLine(intersection, sign);

		if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
			sPlayer.setLineBuffer(ChatColor.stripColor(sign.getLine(clickedLine)));
		} else {
			sPlayer.setLineBuffer(sign.getLine(clickedLine));
		}
		player.sendMessage(signMessages.get(Message.TOOL_LINE_COPIED, sPlayer.getLineBuffer()));
		this.secondaryUseCount++;
	}
}
