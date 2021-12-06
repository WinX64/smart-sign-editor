package io.github.winx64.sse.handler;

import io.github.winx64.sse.data.SignData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Interface specifying version dependent methods to be implemented separately
 */
public interface VersionAdapter {

    /**
     * Set the player as the one editing the sign and opens the sign editor for
     * them
     *
     * @param player the player
     * @param location the location of the sign
     */
    void openSignEditor(Player player, Location location);

    /**
     * Verifies if the given block is a sign of any type (wall-sign or sign-post)
     * @param block the block to be verified
     * @return whether the block is a sign or not
     */
    boolean isSign(Block block);

    /**
     * Builds the corresponding <see>SignData</see> from the given sign
     * @param sign the given sign
     * @return the newly created data
     */
    SignData getSignData(Sign sign);

    /**
     * Verifies if the given sign is currently being edited by someone
     * @param sign the given sign
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
}
