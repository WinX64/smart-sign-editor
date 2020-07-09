package io.github.winx64.sse.data;

import org.bukkit.entity.Player;

public interface PlayerRepository {

    void registerPlayer(Player player);

    SmartPlayer getPlayer(Player player);

    void unregisterPlayer(Player player);
}
