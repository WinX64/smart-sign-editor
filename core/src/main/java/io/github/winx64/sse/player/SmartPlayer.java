package io.github.winx64.sse.player;

import io.github.winx64.sse.tool.Tool;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Data class to store the player information related to SmartSignEditor
 *
 * @author WinX64
 */
public final class SmartPlayer {

    private final Player player;

    private Tool tool;

    private String lineBuffer;
    private String[] signBuffer;

    public SmartPlayer(Player player) {
        this.player = player;
        this.tool = Tool.EDIT;

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
     * Gets the unique id of this player
     *
     * @return The UUID
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * Gets the current tool type this player is using
     *
     * @return The current tool
     */
    public Tool getTool() {
        return tool;
    }

    /**
     * Sets the current tool type this player is using
     *
     * @param tool The new tool
     */
    public void setTool(Tool tool) {
        this.tool = tool;
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
