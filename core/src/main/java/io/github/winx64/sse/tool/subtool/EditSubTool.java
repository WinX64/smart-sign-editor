package io.github.winx64.sse.tool.subtool;

import io.github.winx64.sse.SmartSignEditor;
import io.github.winx64.sse.configuration.SignMessage.NameKey;
import io.github.winx64.sse.player.Permissions;
import io.github.winx64.sse.player.SmartPlayer;
import io.github.winx64.sse.tool.SubTool;
import io.github.winx64.sse.tool.ToolUsage;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public final class EditSubTool extends SubTool {

    public EditSubTool() {
        super("sign-edit", NameKey.TOOL_EDIT_NAME, Permissions.TOOL_EDIT, true, true, ToolUsage.RIGHT_CLICK);
    }

    @Override
    public void use(SmartSignEditor plugin, SmartPlayer sPlayer, Sign sign) {
        Player player = sPlayer.getPlayer();

        if (plugin.getVersionAdapter().isSignBeingEdited(sign)
                && !player.hasPermission(Permissions.TOOL_EDIT_OVERRIDE)) {
            player.sendMessage(plugin.getSignMessage().get(NameKey.OVERRIDE_NO_PERMISSION));
            return;
        }

        String[] noColors = sign.getLines();
        for (int i = 0; i < 4; i++) {
            noColors[i] = noColors[i].replace(ChatColor.COLOR_CHAR, '&');
        }
        plugin.getVersionAdapter().updateSignText(player, sign, noColors);
        plugin.getVersionAdapter().openSignEditor(player, sign);
        this.useCount++;
    }
}
