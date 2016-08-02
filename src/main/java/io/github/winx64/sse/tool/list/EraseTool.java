package io.github.winx64.sse.tool.list;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.github.winx64.sse.MathUtil;
import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class EraseTool extends Tool {

    public EraseTool(SmartSignEditor plugin) {
	super(plugin, ToolType.ERASE, "Sign Erase", "Line Erase", Permissions.TOOL_ERASE_ALL,
		Permissions.TOOL_ERASE_LINE);
    }

    @Override
    public void usePrimary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	for (int i = 0; i < 4; i++) {
	    sign.setLine(i, "");
	}
	sign.update();
	player.sendMessage(ChatColor.GREEN + "Sign cleared!");
    }

    @Override
    public void useSecondary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	Vector intersection = MathUtil.getSightSignIntersection(player, sign);
	if (intersection == null) {
	    player.sendMessage(ChatColor.RED + "Choose a valid line, inside the sign plate!");
	    return;
	}

	int clickedLine = MathUtil.getSignLine(intersection, sign);

	sign.setLine(clickedLine, "");
	sign.update();
	player.sendMessage(ChatColor.GREEN + "Line cleared!");
    }

    @Override
    public boolean preSpecialHandling() {
	return false;
    }
}
