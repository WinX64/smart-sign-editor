package io.github.winx64.sse.tool;

import io.github.winx64.sse.player.SmartPlayer;
import org.bukkit.block.Sign;

public interface Tool {

    String getName();

    String getPermission();

    boolean modifiesWorld();

    boolean requiresSpecialHandling();

    void use(SmartPlayer sPlayer, Sign clickedSign);
}
