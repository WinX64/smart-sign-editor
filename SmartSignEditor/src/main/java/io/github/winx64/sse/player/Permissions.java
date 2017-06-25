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
package io.github.winx64.sse.player;

/**
 * Helper class to store the permissions currently used
 * 
 * @author Lucas
 *
 */
public final class Permissions {

	private Permissions() {}

	/**
	 * Edit/Copy/Paste/Erase signs without directly interacting with them
	 */
	public static final String EXTENDED_TOOL = "sse.tool.extended";

	/**
	 * Use the edit tool
	 */
	public static final String TOOL_EDIT = "sse.tool.edit";

	/**
	 * Modify signs that are currently being edited by others
	 */
	public static final String TOOL_EDIT_OVERRIDE = "sse.tool.edit.override";

	/**
	 * Use colors with the edit tool
	 */
	public static final String TOOL_EDIT_COLORS = "sse.tool.edit.colors";

	/**
	 * Use the copy tool
	 */
	public static final String TOOL_COPY = "sse.tool.copy";

	/**
	 * Use the copy tool to copy lines
	 */
	public static final String TOOL_COPY_LINE = "sse.tool.copy.line";

	/**
	 * Use the copy tool to copy the entire sign
	 */
	public static final String TOOL_COPY_ALL = "sse.tool.copy.all";

	/**
	 * Copy colors with the copy tool
	 */
	public static final String TOOL_COPY_COLORS = "sse.tool.copy.colors";

	/**
	 * Use the paste tool
	 */
	public static final String TOOL_PASTE = "sse.tool.paste";

	/**
	 * Use the paste tool to paste lines
	 */
	public static final String TOOL_PASTE_LINE = "sse.tool.paste.line";

	/**
	 * Use the paste tool to paste the entire sign
	 */
	public static final String TOOL_PASTE_ALL = "sse.tool.paste.all";

	/**
	 * Paste colors with the paste tool
	 */
	public static final String TOOL_PASTE_COLORS = "sse.tool.paste.colors";

	/**
	 * Use the erase tool
	 */
	public static final String TOOL_ERASE = "sse.tool.erase";

	/**
	 * Use the erase tool to clear lines
	 */
	public static final String TOOL_ERASE_LINE = "sse.tool.erase.line";

	/**
	 * Use the erase tool to clear the entire sign
	 * 
	 */
	public static final String TOOL_ERASE_ALL = "sse.tool.erase.all";
}
