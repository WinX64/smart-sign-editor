package io.github.winx64.sse.tool;

import io.github.winx64.sse.player.SmartPlayer;
import org.bukkit.block.Sign;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a tool to be used to interact with signs
 *
 * @author WinX64
 */
@Experimental
public interface Tool {

    /**
     * Gets the name of this tool
     * @return the tool's name
     */
    @NotNull
    String getName();

    /**
     * Gets the permission necessary to use this tool
     * @return the tool's permission
     */
    @NotNull
    String getPermission();

    /**
     * Gets whether or not this tool physically modifies the world
     * @return whether or not it modifies the world
     */
    boolean modifiesWorld();

    /**
     * Gets whether or not this tool requires special signs to be handled before it is used
     * @return whether or not prior handling is required
     */
    boolean requiresPriorSpecialHandling();

    /**
     * Makes the given player use the tool on the given sign
     * @param sPlayer the smart player
     * @param clickedSign the interacted sign
     * @throws NullPointerException if sPlayer is null
     * @throws NullPointerException if clickedSign is null
     */
    void use(@NotNull SmartPlayer sPlayer, @NotNull Sign clickedSign);
}
