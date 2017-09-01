package io.github.winx64.sse.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.Permissions;

public final class SignChangeListener implements Listener {

	public SignChangeListener(SmartSignEditor plugin) {}

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
