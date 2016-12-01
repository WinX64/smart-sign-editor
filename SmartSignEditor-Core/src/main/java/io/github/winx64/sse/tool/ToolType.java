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
package io.github.winx64.sse.tool;

import io.github.winx64.sse.player.Permissions;

/**
 * Represents the diffent kind of tools SmartSignEditor has
 * 
 * @author Lucas
 *
 */
public enum ToolType {

    /**
     * Edit Tool. Used to edit signs
     */
    EDIT("Edit", Permissions.TOOL_EDIT),

    /**
     * Copy Tool. Used to copy sign text
     */
    COPY("Copy", Permissions.TOOL_COPY),

    /**
     * Paste Tool. Used to paste your copied text to a new sign
     */
    PASTE("Paste", Permissions.TOOL_PASTE),

    /**
     * Erase Tool. Used to clear the text from signs
     */
    ERASE("Erase", Permissions.TOOL_ERASE);

    private final String name;
    private final String permission;

    private ToolType(String name, String permission) {
	this.name = name;
	this.permission = permission;
    }

    /**
     * Gets the user-friendly name for this tool
     * 
     * @return The name
     */
    public String getName() {
	return name;
    }

    /**
     * Gets the permission needed to use this tool
     * 
     * @return The permission
     */
    public String getPermission() {
	return permission;
    }

    /**
     * Gets the next tool in the line
     * 
     * @return The next tool
     */
    public ToolType getNextToolMode() {
	int current = ordinal();
	ToolType[] tools = values();

	if (current == tools.length - 1) {
	    return tools[0];
	} else {
	    return tools[current + 1];
	}
    }

    /**
     * Gets the previous tool in the line
     * 
     * @return The previous tool
     */
    public ToolType getPreviousToolMode() {
	int current = ordinal();
	ToolType[] tools = values();

	if (current == 0) {
	    return tools[tools.length - 1];
	} else {
	    return tools[current - 1];
	}
    }
}
