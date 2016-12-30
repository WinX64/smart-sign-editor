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
package io.github.winx64.sse.handler;

import java.util.Collection;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface VersionAdapter {

	/**
	 * Updates a the text of a sign for a specific player
	 * 
	 * @param player
	 *            The player
	 * @param sign
	 *            The sign
	 * @param text
	 *            The new lines of text
	 */
	public void updateSignText(Player player, Sign sign, String[] text);

	/**
	 * Set the player as the one editing the sign and opens the sign editor for
	 * them
	 * 
	 * @param player
	 *            The player
	 * @param sign
	 *            The sign
	 */
	public void openSignEditor(Player player, Sign sign);

	/**
	 * Checks if the specified sign is currently being edited by a player or not
	 * 
	 * @param sign
	 *            The sign
	 * @return Whether the sign is being edited or not
	 */
	public boolean isSignBeingEdited(Sign sign);

	/**
	 * Checks if the plugin should continue processing this event. Mainly to
	 * avoid calling interact events twice due to the dual wielding in newer
	 * versions
	 * 
	 * @param event The interact event
	 * @return If it should continue
	 */
	public boolean shouldProcessEvent(PlayerInteractEvent event);

	/**
	 * Older builds don't have the new method, and newer builds don't have the
	 * old method. This one should work for all of them.
	 * 
	 * @return The online players
	 */
	public Collection<? extends Player> getOnlinePlayers();
}
