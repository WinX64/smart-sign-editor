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
package io.github.winx64.sse.player;

import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Data class to store the player information related to SmartSignEditor
 * 
 * @author Lucas
 *
 */
public class SmartPlayer {

    private Player player;

    private ToolMode toolMode;

    private String lineBuffer;
    private String[] textBuffer;

    public SmartPlayer(Player player) {
	this.player = player;
	this.toolMode = ToolMode.EDIT;

	this.lineBuffer = null;
	this.textBuffer = null;
    }

    /**
     * Gets the Player linked to this SmartPlayer
     * 
     * @return The Player instance
     */
    public Player getPlayer() {
	return player;
    }

    /**
     * Gets the unique id of this player
     * 
     * @return The UUID
     */
    public UUID getUniqueId() {
	return player.getUniqueId();
    }

    /**
     * Gets the name of this player
     * 
     * @return The name
     */
    public String getName() {
	return player.getName();
    }

    /**
     * Gets the current tool mode this player is using
     * 
     * @return The current tool
     */
    public ToolMode getToolMode() {
	return toolMode;
    }

    /**
     * Sets the current tool mode this player is using
     * 
     * @param toolMode
     *            The new tool
     */
    public void setToolMode(ToolMode toolMode) {
	this.toolMode = toolMode;
    }

    /**
     * Gets the last copied line
     * 
     * @return The line
     */
    public String getLineBuffer() {
	return lineBuffer;
    }

    /**
     * Sets the last copied line
     * 
     * @param lineBuffer
     *            The new line
     */
    public void setLineBuffer(String lineBuffer) {
	this.lineBuffer = lineBuffer;
    }

    /**
     * Gets the last copied sign text
     * 
     * @return The entire sign text
     */
    public String[] getTextBuffer() {
	return textBuffer;
    }

    /**
     * Sets a specific line of the copied sign text
     * 
     * @param line
     *            The line
     * @param text
     *            The new text
     */
    public void setTextBuffer(int line, String text) {
	if (textBuffer == null) {
	    this.textBuffer = new String[4];
	}
	this.textBuffer[line] = text;
    }

    /**
     * Sets the last copied sign text
     * 
     * @param textBuffer
     *            The new sign text
     */
    public void setTextBuffer(String[] textBuffer) {
	this.textBuffer = textBuffer;
    }
}
