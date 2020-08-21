package io.github.winx64.sse.player;

import io.github.winx64.sse.tool.ToolCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Data class to store the player information related to SmartSignEditor
 *
 * @author WinX64
 */
@Experimental
public interface SmartPlayer {

    /**
     * Gets the Player associated with this SmartPlayer
     *
     * @return The Player instance
     */
    @NotNull
    Player getPlayer();

    /**
     * Gets the current tool category this player is using
     *
     * @return The current tool
     */
    @NotNull
    ToolCategory getSelectedToolCategory();

    /**
     * Sets the current tool category this player is using
     *
     * @param toolCategory The new category
     * @throws NullPointerException if toolCategory is null
     */
    void setSelectedToolCategory(@NotNull ToolCategory toolCategory);

    /**
     * Gets the last copied line
     *
     * @return The line
     */
    @Nullable
    String getLineBuffer();

    /**
     * Sets the last copied line
     *
     * @param lineBuffer The new line
     */
    void setLineBuffer(@Nullable String lineBuffer);

    /**
     * Gets the last copied sign text
     *
     * @return The entire sign text
     */
    @Nullable
    List<String> getSignBuffer();

    /**
     * Sets a specific line of the copied sign text
     *
     * @param signBuffer The new lines
     */
    void setSignBuffer(@Nullable List<String> signBuffer);
}
