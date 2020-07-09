package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.configuration.SignMessage;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.data.SmartPlayer;
import io.github.winx64.sse.handler.VersionAdapter;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public final class EditSubTool extends SubTool {

    public EditSubTool() {
        super("sign-edit", NameKey.TOOL_EDIT_NAME, Permissions.TOOL_EDIT, true, true, ToolUsage.RIGHT_CLICK);
    }

    @Override
    public void use(VersionAdapter adapter, SignMessage signMessage, SmartPlayer sPlayer, Block clickedSign) {
        Player player = sPlayer.getPlayer();

        if (adapter.isSignBeingEdited(clickedSign) && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
            player.sendMessage(signMessage.get(NameKey.OVERRIDE_NO_PERMISSION));
            return;
        }

        Sign sign = (Sign) clickedSign.getState();
        String[] noColors = new String[4];
        for (int i = 0; i < 4; i++) {
            noColors[i] = sign.getLine(i).replace(ChatColor.COLOR_CHAR, '&');
        }

        Location location = clickedSign.getLocation();
        adapter.updateSignText(player, location, noColors);
        adapter.openSignEditor(player, location);
        this.useCount++;
    }
}
