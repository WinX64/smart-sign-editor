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
import org.bukkit.util.Vector;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.Permissions;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.player.ToolMode;

public class SignInteractionListener implements Listener {

    private SmartSignEditor plugin;

    private BlockState lastSignState;

    public SignInteractionListener(SmartSignEditor plugin) {
	this.plugin = plugin;
	this.lastSignState = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
	Player player = event.getPlayer();
	SmartPlayer sPlayer = plugin.getSmartPlayer(player.getUniqueId());
	Action a = event.getAction();

	if (sPlayer.getSignCooldown() > System.currentTimeMillis()) {
	    return;
	}

	if (player.getItemInHand().getType() != Material.FEATHER) {
	    return;
	}

	if (a == Action.RIGHT_CLICK_BLOCK) {
	    Block block = event.getClickedBlock();
	    if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
		Sign sign = (Sign) block.getState();
		event.setCancelled(true);

		switch (sPlayer.getToolMode()) {
		    case EDIT:
			handleSpecialSigns(sign);
			handleEdit(sPlayer, sign);
			break;

		    case COPY:
			handleCopy(sPlayer, sign);
			handleSpecialSigns(sign);
			break;

		    case PASTE:
			handlePaste(sPlayer, sign);
			handleSpecialSigns(sign);
			break;

		    case ERASE:
			handleErase(sPlayer, sign);
			handleSpecialSigns(sign);
			break;
		}
	    } else {
		changeToolMode(sPlayer);
	    }
	} else if (a == Action.RIGHT_CLICK_AIR) {
	    changeToolMode(sPlayer);
	}
	sPlayer.setSignCooldown(System.currentTimeMillis() + 100);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void postProcess(PlayerInteractEvent event) {
	if (lastSignState != null) {
	    lastSignState.update();
	    this.lastSignState = null;
	}
    }

    /**
     * Handles sign edits
     * 
     * @param sPlayer
     *            The SmartPlayer instance
     * @param sign
     *            The sign
     * @param event
     *            The interaction event
     */
    private void handleEdit(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (!player.hasPermission(Permissions.TOOL_EDIT)) {
	    player.sendMessage(ChatColor.RED + "You don't have permission to use the Edit Tool!");
	    return;
	}

	if (!checkBuildPermission(player, sign)) {
	    return;
	}

	String[] noColors = sign.getLines();
	for (int i = 0; i < 4; i++) {
	    noColors[i] = noColors[i].replace(ChatColor.COLOR_CHAR, '&');
	}
	plugin.getVersionHandler().updateSignText(player, sign, noColors);
	plugin.getVersionHandler().openSignEditor(player, sign);
    }

    /**
     * Handles sign copies
     * 
     * @param sPlayer
     *            The SmartPlayer instance
     * @param sign
     *            The sign
     * @param event
     *            The interaction event
     */
    private void handleCopy(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (!player.hasPermission(Permissions.TOOL_COPY)) {
	    player.sendMessage(ChatColor.RED + "You don't have permission to use the Copy Tool!");
	    return;
	}

	if (player.isSneaking()) {
	    if (!player.hasPermission(Permissions.TOOL_COPY_LINE)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to copy single lines!");
		return;
	    }
	    Vector intersection = MathUtil.getSightSignIntersection(player, sign);
	    if (intersection == null) {
		player.sendMessage(ChatColor.RED + "Choose a valid line, inside the sign plate!");
		return;
	    }
	    int clickedLine = MathUtil.getSignLine(intersection, sign);

	    if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
		sPlayer.setLineBuffer(ChatColor.stripColor(sign.getLine(clickedLine)));
	    } else {
		sPlayer.setLineBuffer(sign.getLine(clickedLine));
	    }
	    player.sendMessage(ChatColor.GREEN + "Line text copied: " + ChatColor.RESET + sPlayer.getLineBuffer());
	} else {
	    if (!player.hasPermission(Permissions.TOOL_COPY_ALL)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to copy the entire sign!");
		return;
	    }
	    for (int i = 0; i < 4; i++) {
		if (!player.hasPermission(Permissions.TOOL_COPY_COLORS)) {
		    sPlayer.setTextBuffer(i, ChatColor.stripColor(sign.getLine(i)));
		} else {
		    sPlayer.setTextBuffer(i, sign.getLine(i));
		}
	    }
	    player.sendMessage(ChatColor.GREEN + "Sign text copied!");
	}
    }

    /**
     * Handles sign pastes
     * 
     * @param sPlayer
     *            The SmartPlayer instance
     * @param sign
     *            The sign
     * @param event
     *            The interaction event
     */
    private void handlePaste(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (!player.hasPermission(Permissions.TOOL_PASTE)) {
	    player.sendMessage(ChatColor.RED + "You don't have permission to use the Paste Tool!");
	    return;
	}

	if (!checkBuildPermission(player, sign)) {
	    return;
	}

	if (player.isSneaking()) {
	    if (!player.hasPermission(Permissions.TOOL_PASTE_LINE)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to paste single lines!");
		return;
	    }

	    if (sPlayer.getLineBuffer() == null) {
		player.sendMessage(ChatColor.RED + "You haven't copied any line yet!");
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
	} else {
	    if (!player.hasPermission(Permissions.TOOL_PASTE_ALL)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to paste the entire sign!");
		return;
	    }

	    if (sPlayer.getTextBuffer() == null) {
		player.sendMessage(ChatColor.RED + "You haven't copied any sign yet!");
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
    }

    /**
     * Handles sign erases
     * 
     * @param sPlayer
     *            The SmartPlayer instance
     * @param sign
     *            The sign
     * @param event
     *            The interaction event
     */
    private void handleErase(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	if (!player.hasPermission(Permissions.TOOL_ERASE)) {
	    player.sendMessage(ChatColor.RED + "You don't have permission to use the Erase Tool!");
	    return;
	}

	if (!checkBuildPermission(player, sign)) {
	    return;
	}

	if (player.isSneaking()) {
	    if (!player.hasPermission(Permissions.TOOL_ERASE_LINE)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to erase single lines!");
		return;
	    }

	    Vector intersection = MathUtil.getSightSignIntersection(player, sign);
	    if (intersection == null) {
		player.sendMessage(ChatColor.RED + "Choose a valid line, inside the sign plate!");
		return;
	    }

	    int clickedLine = MathUtil.getSignLine(intersection, sign);

	    sign.setLine(clickedLine, "");
	    sign.update();
	    player.sendMessage(ChatColor.GREEN + "Line cleared!");
	} else {
	    if (!player.hasPermission(Permissions.TOOL_ERASE_ALL)) {
		player.sendMessage(ChatColor.RED + "You don't have permission to erase the entire sign!");
		return;
	    }

	    for (int i = 0; i < 4; i++) {
		sign.setLine(i, "");
	    }
	    sign.update();
	    player.sendMessage(ChatColor.GREEN + "Sign cleared!");
	}
    }

    /**
     * Handles special sign so it doesn't conflict with other plugins<br>
     * Save the current sign state. Set it to a neutral state. Wait for the
     * other plugins to process the event. Revert back the sign state
     * 
     * @param sign
     *            The sign
     */
    private void handleSpecialSigns(Sign sign) {
	String firstLine = ChatColor.stripColor(sign.getLine(0));
	if (plugin.isSpecialSign(firstLine)) {
	    this.lastSignState = sign.getBlock().getState();
	    sign.setLine(0, firstLine);
	    sign.update();
	}
    }

    /**
     * Change the player's tool based on their permissions
     * 
     * @param sPlayer
     *            The SmartPlayer instance
     */
    private void changeToolMode(SmartPlayer sPlayer) {
	Player player = sPlayer.getPlayer();
	ToolMode currentTool = sPlayer.getToolMode();
	ToolMode newTool = currentTool;
	while (true) {
	    newTool = newTool.getNextToolMode();
	    if (newTool == currentTool || player.hasPermission(newTool.getPermission())) {
		break;
	    }
	}

	if (!player.hasPermission(newTool.getPermission())) {
	    return;
	}

	sPlayer.setToolMode(newTool);
	sPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "Tool Mode: " + ChatColor.AQUA + newTool.getName());
    }

    /**
     * Manually fires a BlockBreakEvent to check if other plugins permit this
     * player to modify the area where the sign is located at<br>
     * If modification is not allowed, edits/pastes/erases are not allowed as
     * well
     * 
     * @param player
     *            The player modifying the sign
     * @param sign
     *            The sign
     * @return Whether the player can or not modify this area
     */
    private boolean checkBuildPermission(Player player, Sign sign) {
	BlockBreakEvent event = new BlockBreakEvent(sign.getBlock(), player);
	plugin.getServer().getPluginManager().callEvent(event);
	return !event.isCancelled();
    }
}
