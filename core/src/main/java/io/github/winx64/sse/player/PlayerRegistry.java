package io.github.winx64.sse.player;

import org.bukkit.entity.Player;

public interface PlayerRegistry {

    SmartPlayer getPlayer(Player player);
}
