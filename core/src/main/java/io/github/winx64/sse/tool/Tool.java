package io.github.winx64.sse.tool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.tool.subtool.CopySubTool;
import io.github.winx64.sse.tool.subtool.EditSubTool;
import io.github.winx64.sse.tool.subtool.EraseSubTool;
import io.github.winx64.sse.tool.subtool.PasteSubTool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the different kind of subtool SmartSignEditor has
 *
 * @author WinX64
 */
public enum Tool {

    /**
     * Edit Tool. Used to edit signs
     */
    EDIT("edit", NameKey.TOOL_EDIT_NAME, Permissions.TOOL_EDIT),

    /**
     * Copy Tool. Used to copy sign text
     */
    COPY("copy", NameKey.TOOL_COPY_NAME, Permissions.TOOL_COPY),

    /**
     * Paste Tool. Used to paste your copied text to a new sign
     */
    PASTE("paste", NameKey.TOOL_PASTE_NAME, Permissions.TOOL_PASTE),

    /**
     * Erase Tool. Used to clear the text from signs
     */
    ERASE("erase", SignMessage.NameKey.TOOL_ERASE_NAME, Permissions.TOOL_ERASE);

    static {
        EDIT.registerSubTool(new EditSubTool());

        COPY.registerSubTool(new CopySubTool.SignCopySubTool());
        COPY.registerSubTool(new CopySubTool.LineCopySubTool());

        PASTE.registerSubTool(new PasteSubTool.SignPasteSubTool());
        PASTE.registerSubTool(new PasteSubTool.LinePasteSubTool());

        ERASE.registerSubTool(new EraseSubTool.SignEraseSubTool());
        ERASE.registerSubTool(new EraseSubTool.LineEraseSubTool());
    }

    private final String id;
    private final NameKey nameKey;
    private final String permission;
    private final Map<String, SubTool> subTools;

    Tool(String id, NameKey nameKey, String permission) {
        this.id = id;
        this.nameKey = nameKey;
        this.permission = permission;
        this.subTools = new HashMap<>();
    }

    private void registerSubTool(SubTool subTool) {
        this.subTools.put(subTool.getId().toLowerCase(), subTool);
    }

    /**
     * Gets the identification name for this tool
     *
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the name key for this tool
     *
     * @return The nameKey
     */
    public NameKey getNameKey() {
        return nameKey;
    }

    /**
     * Gets the user-friendly name for this tool
     *
     * @param signMessage The SignMessage instance
     * @return The name
     */
    public String getName(SignMessage signMessage) {
        return signMessage.get(nameKey);
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
     * Returns every subtool associated with this tool
     *
     * @return The subTools
     */
    public Map<String, SubTool> getSubTools() {
        return Collections.unmodifiableMap(subTools);
    }

    public int getTotalUseCount() {
        return subTools.values().stream().mapToInt(SubTool::getUseCount).sum();
    }

    public SubTool matchesUsage(ToolUsage usage) {
        return subTools.values().stream().filter((subTool) -> usage.matchesWith(subTool.getUsage())).findFirst().orElse(null);
    }

    /**
     * Gets the next tool in the line
     *
     * @return The next tool
     */
    public Tool getNextToolMode() {
        int current = ordinal();
        Tool[] tools = values();

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
    public Tool getPreviousToolMode() {
        int current = ordinal();
        Tool[] tools = values();

        if (current == 0) {
            return tools[tools.length - 1];
        } else {
            return tools[current - 1];
        }
    }
}
