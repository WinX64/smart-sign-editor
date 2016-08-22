package io.github.winx64.sse.tool.tools;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class ToolChange extends Tool {

    public ToolChange(SmartSignEditor plugin) {
	super(plugin, null, "Next Tool", "Previous Tool", null, null);
    }

    @Override
    public void usePrimary(SmartPlayer sPlayer, Sign sign) {
	changeTool(sPlayer, true);
    }

    @Override
    public void useSecondary(SmartPlayer sPlayer, Sign sign) {
	changeTool(sPlayer, false);
    }

    @Override
    public boolean preSpecialHandling() {
	return true;
    }

    private void changeTool(SmartPlayer sPlayer, boolean foward) {
	Player player = sPlayer.getPlayer();
	ToolType currentTool = sPlayer.getToolType();
	ToolType newTool = currentTool;
	while (true) {
	    newTool = foward ? newTool.getNextToolMode() : newTool.getPreviousToolMode();
	    if (newTool == currentTool || player.hasPermission(newTool.getPermission())) {
		break;
	    }
	}

	if (!player.hasPermission(newTool.getPermission())) {
	    return;
	}

	sPlayer.setToolMode(newTool);
	sPlayer.getPlayer().sendMessage(ChatColor.YELLOW + "Tool Mode: " + ChatColor.AQUA + newTool.getName());
    }
}
