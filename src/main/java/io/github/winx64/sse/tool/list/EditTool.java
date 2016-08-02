package io.github.winx64.sse.tool.list;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.Tool;
import io.github.winx64.sse.tool.ToolType;

public final class EditTool extends Tool {

    public EditTool(SmartSignEditor plugin) {
	super(plugin, ToolType.EDIT, null, null, null, null);
    }

    @Override
    public void usePrimary(SmartPlayer sPlayer, Sign sign) {
	Player player = sPlayer.getPlayer();

	String[] noColors = sign.getLines();
	for (int i = 0; i < 4; i++) {
	    noColors[i] = noColors[i].replace(ChatColor.COLOR_CHAR, '&');
	}
	plugin.getVersionHandler().updateSignText(player, sign, noColors);
	plugin.getVersionHandler().openSignEditor(player, sign);
    }

    @Override
    public void useSecondary(SmartPlayer sPlayer, Sign sign) {
	this.usePrimary(sPlayer, sign);
    }

    @Override
    public boolean preSpecialHandling() {
	return true;
    }
}
