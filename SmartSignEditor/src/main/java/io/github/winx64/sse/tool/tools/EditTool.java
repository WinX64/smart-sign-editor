package io.github.winx64.sse.tool.tools;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessages.Message;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class EditTool extends Tool {

	public EditTool(SmartSignEditor plugin) {
		super(plugin, ToolType.EDIT, "Sign Edit", null, Permissions.TOOL_EDIT, null, true, true);
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
		this.primaryUseCount++;
	}

	@Override
	public void useSecondary(SmartPlayer sPlayer, Sign sign) {
		this.usePrimary(sPlayer, sign);
	}
}
