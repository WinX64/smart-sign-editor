package io.github.winx64.sse.player;

import io.github.winx64.sse.tool.ToolCategory;
import org.bukkit.entity.Player;

/**
 * Data class to store the player information related to SmartSignEditor
 *
 * @author WinX64
 */
public final class SmartPlayer {

    private final Player player;

    private ToolCategory selectedCategory;

    private String lineBuffer;
    private String[] signBuffer;

    public SmartPlayer(Player player, ToolCategory selectedCategory) {
        this.player = player;

        this.selectedCategory = selectedCategory;

        this.lineBuffer = null;
        this.signBuffer = null;
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
     * Gets the current tool category this player is using
     *
     * @return The current tool
     */
    public ToolCategory getSelectedToolCategory() { return selectedCategory; }

    /**
     * Sets the current tool category this player is using
     *
     * @param toolCategory The new category
     */
    public void setSelectedToolCategory(ToolCategory toolCategory) {
        this.selectedCategory = toolCategory;
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
     * @param lineBuffer The new line
     */
    public void setLineBuffer(String lineBuffer) {
        this.lineBuffer = lineBuffer;
    }

    /**
     * Gets the last copied sign text
     *
     * @return The entire sign text
     */
    public String[] getSignBuffer() {
        return signBuffer;
    }

    /**
     * Sets a specific line of the copied sign text
     *
     * @param line The line
     * @param text The new text
     */
    public void setSignBuffer(int line, String text) {
        if (signBuffer == null) {
            this.signBuffer = new String[4];
        }
        this.signBuffer[line] = text;
    }
}
