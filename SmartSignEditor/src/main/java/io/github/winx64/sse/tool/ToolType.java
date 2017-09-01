package io.github.winx64.sse.tool;

import io.github.winx64.sse.player.Permissions;

/**
 * Represents the diffent kind of tools SmartSignEditor has
 * 
 * @author WinX64
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
