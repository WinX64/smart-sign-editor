package io.github.winx64.sse.player;

import org.bukkit.entity.Player;

public interface PlayerRegistry {

    void registerPlayer(Player player);

    SmartPlayer getPlayer(Player player);

    void unregisterPlayer(Player player);
}
