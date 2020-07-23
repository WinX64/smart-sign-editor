package io.github.winx64.sse.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A registry that holds all player instances of SmartSignEditor
 *
 * @author WinX64
 */
public interface PlayerRegistry {

    /**
     * Gets the SmartPlayer associated with the given Player
     * @param player the player
     * @return the SmartPlayer, or null if there isn't one
     */
    @Nullable
    SmartPlayer getPlayer(@NotNull Player player);
}
