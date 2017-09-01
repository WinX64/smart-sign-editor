package io.github.winx64.sse.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignConfiguration;
import io.github.winx64.sse.configuration.SignMessages;
import io.github.winx64.sse.configuration.SignMessages.Message;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolUsage;

public final class SignInteractionListener implements Listener {

	private final SmartSignEditor plugin;
	private final SignConfiguration signConfig;
	private final SignMessages signMessages;

	private final Map<PlayerInteractEvent, BlockState> blockStates;

	public SignInteractionListener(SmartSignEditor plugin) {
		this.plugin = plugin;
		this.signConfig = plugin.getSignConfig();
		this.signMessages = plugin.getSignMessages();
		this.blockStates = new HashMap<PlayerInteractEvent, BlockState>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		SmartPlayer sPlayer = plugin.getSmartPlayer(player.getUniqueId());
		if (sPlayer == null) {
			return;
		}

		Tool tool = plugin.getTool(sPlayer.getToolType());
		Block block = event.getClickedBlock();
		Action action = event.getAction();
		ToolUsage usage = ToolUsage.getToolUsage(action, player.isSneaking());

		if (action == Action.PHYSICAL) {
			return;
		}

		if (!plugin.getVersionAdapter().shouldProcessEvent(event)) {
			return;
		}

		if (!this.signConfig.matchesItem(player.getItemInHand())) {
			return;
		}

		if (block == null && player.hasPermission(Permissions.EXTENDED_TOOL) && signConfig.isUsingExtendedTool()) {
			if (usage.matchesWith(tool.getPrimaryUsage()) || usage.matchesWith(tool.getSecondaryUsage())) {
				BlockIterator iter = new BlockIterator(player, signConfig.getExtendedToolReach());
				while (iter.hasNext()) {
					Block tracedBlock = iter.next();
					if (tracedBlock.getType() == Material.SIGN_POST || tracedBlock.getType() == Material.WALL_SIGN) {
						block = tracedBlock;
						break;
					}
				}
			}
		}

		if (block == null || (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)) {
			Tool toolChanger = plugin.getTool(null);
			if (toolChanger.getPrimaryUsage().matchesWith(usage)) {
				toolChanger.usePrimary(sPlayer, null);
			} else if (toolChanger.getSecondaryUsage().matchesWith(usage)) {
				toolChanger.useSecondary(sPlayer, null);
			}
		} else {
			Sign sign = (Sign) block.getState();
			if (tool.modifiesWorld() && !checkBuildPermission(player, sign)) {
				return;
			}

			if (!player.hasPermission(tool.getType().getPermission())) {
				player.sendMessage(signMessages.get(Message.TOOL_NO_PERMISSION, tool.getType().getName()));
				return;
			}

			event.setCancelled(true);

			if (tool.requiresPreSpecialHandling()) {
				this.handleSpecialSigns(event, sign);
			}
			if (usage.matchesWith(tool.getPrimaryUsage())) {
				if (tool.getPrimaryPermission() != null && !player.hasPermission(tool.getPrimaryPermission())) {
					player.sendMessage(signMessages.get(Message.SUB_TOOL_NO_PERMISSION, tool.getPrimaryName()));
				} else {
					tool.usePrimary(sPlayer, sign);
				}
			} else if (usage.matchesWith(tool.getSecondaryUsage())) {
				if (tool.getSecondaryPermission() != null && !player.hasPermission(tool.getSecondaryPermission())) {
					player.sendMessage(signMessages.get(Message.SUB_TOOL_NO_PERMISSION, tool.getSecondaryName()));
				} else {
					tool.useSecondary(sPlayer, sign);
				}
			}
			if (!tool.requiresPreSpecialHandling()) {
				this.handleSpecialSigns(event, sign);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void postProcess(PlayerInteractEvent event) {
		BlockState lastSignState = blockStates.remove(event);
		if (lastSignState != null) {
			lastSignState.update();
		}
	}

	private void handleSpecialSigns(PlayerInteractEvent event, Sign sign) {
		String firstLine = ChatColor.stripColor(sign.getLine(0));
		if (signConfig.isSpecialSign(firstLine)) {
			blockStates.put(event, sign.getBlock().getState());
			sign.setLine(0, firstLine);
			sign.update(true);
		}
	}

	private boolean checkBuildPermission(Player player, Sign sign) {
		BlockBreakEvent event = new BlockBreakEvent(sign.getBlock(), player);
		Bukkit.getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
}
