package io.github.winx64.sse.player;

/**
 * Helper class to store the permissions currently used
 * 
 * @author WinX64
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
