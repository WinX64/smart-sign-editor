package io.github.winx64.sse.handler;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface VersionAdapter {

    /**
     * Updates a the text of a sign for a specific player
     *
     * @param player the player
     * @param sign the sign
     * @param text the new lines of text
     */
    void updateSignText(Player player, Sign sign, String[] text);

    /**
     * Set the player as the one editing the sign and opens the sign editor for
     * them
     *
     * @param player the player
     * @param sign the sign
     */
    void openSignEditor(Player player, Sign sign);

    /**
     * Checks if the specified sign is currently being edited by a player or not
     *
     * @param sign the sign
     * @return whether the sign is being edited or not
     */
    boolean isSignBeingEdited(Sign sign);

    /**
     * Checks if the plugin should continue processing this event. Mainly to
     * avoid calling interact events twice due to the dual wielding in newer
     * versions
     *
     * @param event the interact event
     * @return if it should continue
     */
    boolean shouldProcessEvent(PlayerInteractEvent event);

    /**
     * Creates the respective <code>MaterialData</code> instance for this <code>Sign</code>
     * @param sign The sign to be used
     * @return the respective <code>MaterialData</code>
     */
    org.bukkit.material.Sign buildSignMaterialData(Sign sign);
}
